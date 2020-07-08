package dev.cheerfun.pixivic.biz.recommend.service;

import dev.cheerfun.pixivic.biz.recommend.mapper.RecommendMapper;
import lombok.extern.slf4j.Slf4j;
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
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/7 3:18 下午
 * @description RecommendService
 */
@Service
@Slf4j
public abstract class RecommendService {
    @Autowired
    protected DataSource dataSource;
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    @Autowired
    protected RecommendMapper recommendMapper;

    protected boolean recommend() throws TasteException {
        log.info("开始生成推荐，当前内存消耗" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + " M");
        boolean result = dealRecommender(generateRecommender(generetaDataModel()));
        log.info("生成推荐结束，当前内存消耗" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + " M");
        System.gc();
        return result;
    }

    //获取格式数据
    protected abstract DataModel generetaDataModel() throws TasteException;

    //处理
    protected abstract Recommender generateRecommender(DataModel dataModel) throws TasteException;

    //后续操作
    protected abstract boolean dealRecommender(Recommender recommender) throws TasteException;
}
