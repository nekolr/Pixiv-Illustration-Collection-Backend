package dev.cheerfun.pixivic.biz.recommend.service;

import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLBooleanPrefJDBCDataModel;
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
import java.io.File;
import java.io.IOException;

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

    //获取格式数据

    //处理

    //持久化

    //@PostConstruct
    public void analysis() throws TasteException, IOException {
        System.out.println(Runtime.getRuntime().freeMemory() / 1024 / 1024 + " M");
        System.out.println(Runtime.getRuntime().totalMemory() / 1024 / 1024 + " M");
        System.out.println(Runtime.getRuntime().maxMemory() / 1024 / 1024 + " M");
        MySQLBooleanPrefJDBCDataModel mySQLBooleanPrefJDBCDataModel = new MySQLBooleanPrefJDBCDataModel(dataSource, "illust_recommend", "user_id", "illust_id", "create_date");
        DataModel model = new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(mySQLBooleanPrefJDBCDataModel.exportWithPrefs()));
        RecommenderIRStatsEvaluator evaluator =
                new GenericRecommenderIRStatsEvaluator();
        RecommenderBuilder recommenderBuilder = dataModel -> {
            UserSimilarity similarity = new LogLikelihoodSimilarity(model);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
            return new GenericBooleanPrefUserBasedRecommender(model, neighborhood, similarity);
        };
        Recommender recommender = recommenderBuilder.buildRecommender(model);
        System.out.println(Runtime.getRuntime().freeMemory() / 1024 / 1024 + " M");
        System.out.println(Runtime.getRuntime().totalMemory() / 1024 / 1024 + " M");
        System.out.println(Runtime.getRuntime().maxMemory() / 1024 / 1024 + " M");
        recommender.recommend(53, 1000).forEach(e -> System.out.println(e.getItemID()));
        System.gc();
        System.out.println(Runtime.getRuntime().freeMemory() / 1024 / 1024 + " M");
        System.out.println(Runtime.getRuntime().totalMemory() / 1024 / 1024 + " M");
        System.out.println(Runtime.getRuntime().maxMemory() / 1024 / 1024 + " M");
    }
}
