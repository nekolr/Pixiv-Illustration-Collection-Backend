package dev.cheerfun.pixivic.biz.recommend.service;

import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/7 3:18 下午
 * @description RecommendService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecommendService {
    private final DataSource dataSource;

    @PostConstruct
    public void analysis() throws TasteException {
  /*      new GenericBooleanPrefUserBasedRecommender()
        JDBCDataModel model = new MySQLBooleanPrefDataModel(dataSource,"rc_view","user_id","illust_id","socre","create_date");
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);//用PearsonCorrelation 算法计算用户相似度
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, similarity, model);//计算用户的“邻居”，这里将与该用户最近距离为 3 的用户设置为该用户的“邻居”。
        Recommender recommender = new CachingRecommender(new GenericUserBasedRecommender(model, neighborhood, similarity));//构造推荐引擎，采用 CachingRecommender 为 RecommendationItem 进行缓存
        List<RecommendedItem> recommend = recommender.recommend(53, 100);//得到推荐的结果，size是推荐接过的数目
        recommend.forEach(e->{
            System.out.println(e.getItemID());
        });
*/

        FastByIDMap fastByIDMap = new FastByIDMap();
        DataModel model = new GenericBooleanPrefDataModel();
        RecommenderIRStatsEvaluator evaluator =
                new GenericRecommenderIRStatsEvaluator();
        RecommenderBuilder recommenderBuilder = dataModel -> {
            UserSimilarity similarity = new LogLikelihoodSimilarity(model);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
            return new GenericBooleanPrefUserBasedRecommender(model, neighborhood, similarity);
        };
        Recommender recommender = recommenderBuilder.buildRecommender(model);

    }
}
