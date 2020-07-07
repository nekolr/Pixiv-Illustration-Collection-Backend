package dev.cheerfun.pixivic.biz.recommend.service;

import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.MySQLJDBCIDMigrator;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

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

    public void analysis() throws TasteException {
        JDBCDataModel model = new MySQLJDBCDataModel(dataSource);
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);//用PearsonCorrelation 算法计算用户相似度
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, similarity, model);//计算用户的“邻居”，这里将与该用户最近距离为 3 的用户设置为该用户的“邻居”。
        Recommender recommender = new CachingRecommender(new GenericUserBasedRecommender(model, neighborhood, similarity));//构造推荐引擎，采用 CachingRecommender 为 RecommendationItem 进行缓存
        List<RecommendedItem> recommend = recommender.recommend(53, 100);//得到推荐的结果，size是推荐接过的数目

    }
}
