package dev.cheerfun.pixivic.biz.recommend.service;

import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLBooleanPrefJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/8 10:05 下午
 * @description IllustBookmarkRecommender
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IllustBookmarkRecommendService extends RecommendService {
    @Override
    protected DataModel generetaDataModel() throws TasteException {
        MySQLBooleanPrefJDBCDataModel mySQLBooleanPrefJDBCDataModel = new MySQLBooleanPrefJDBCDataModel(dataSource, "user_illust_bookmarked", "user_id", "illust_id", "create_date");
        DataModel model = new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(mySQLBooleanPrefJDBCDataModel.exportWithPrefs()));
        return model;
    }

    @Override
    protected Recommender generateRecommender(DataModel model) throws TasteException {
        RecommenderIRStatsEvaluator evaluator =
                new GenericRecommenderIRStatsEvaluator();
        RecommenderBuilder recommenderBuilder = dataModel -> {
            UserSimilarity similarity = new LogLikelihoodSimilarity(model);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
            return new GenericBooleanPrefUserBasedRecommender(model, neighborhood, similarity);
        };
        return recommenderBuilder.buildRecommender(model);
    }

    @Override
    protected boolean dealRecommender(Recommender recommender) throws TasteException {
        //清理所有推荐
        Iterator<RedisClusterNode> iterator = stringRedisTemplate.getConnectionFactory().getClusterConnection().clusterGetNodes().iterator();
        while (iterator.hasNext()) {
            RedisClusterNode clusterNode = iterator.next();
            Set<String> keys = stringRedisTemplate.opsForCluster().keys(clusterNode, RedisKeyConstant.USER_RECOMMEND_BOOKMARK_ILLUST + "*");
            stringRedisTemplate.unlink(keys);
        }
        // stringRedisTemplate.unlink(RedisKeyConstant.USER_RECOMMEND_BOOKMARK_ILLUST + "*");
        //根据活跃度分级生成
        LocalDate now = LocalDate.now();
        String today = now.plusDays(2).toString();
        String threeDaysAgo = now.plusDays(-3).toString();
        String sixDaysAgo = now.plusDays(-6).toString();
        String twentyDaysAgo = now.plusDays(-12).toString();
      /*  近三天有行为

        生成30*30个推荐查看作品

        生成30*30个推荐收藏作品

        生成10*30个推荐画师*/
        List<Integer> u1 = recommendMapper.queryUserIdByDateRange(threeDaysAgo, today);
        dealPerUser(u1, recommender, 30);
     /*
        3-6天

        生成30*10个推荐查看作品

        生成30*10个推荐收藏作品

        生成10*10个推荐画师*/
        List<Integer> u2 = recommendMapper.queryUserIdByDateRange(sixDaysAgo, threeDaysAgo);
        dealPerUser(u2, recommender, 15);

     /*   6-12天

        生成30*10个推荐查看作品

        生成30*10个推荐收藏作品

        生成10*10个推荐画师*/
        List<Integer> u3 = recommendMapper.queryUserIdByDateRange(twentyDaysAgo, sixDaysAgo);
        dealPerUser(u3, recommender, 10);
        return true;
    }

    private void dealPerUser(List<Integer> u, Recommender recommender, Integer size) {
        u.forEach(e -> {
            try {
                System.out.println("开始持久化用户：" + e + "的推荐列表");
                List<RecommendedItem> recommend = recommender.recommend(e, 30 * size);
                Set<ZSetOperations.TypedTuple<String>> typedTuples = recommend.stream().map(recommendedItem -> new DefaultTypedTuple<>(String.valueOf(recommendedItem.getItemID()), (double) recommendedItem.getValue())).collect(Collectors.toSet());
                if (typedTuples.size() > 0) {
                    stringRedisTemplate.opsForZSet().add(RedisKeyConstant.USER_RECOMMEND_BOOKMARK_ILLUST + e, typedTuples);
                }
            } catch (TasteException tasteException) {
                tasteException.printStackTrace();
            }
        });
    }
}
