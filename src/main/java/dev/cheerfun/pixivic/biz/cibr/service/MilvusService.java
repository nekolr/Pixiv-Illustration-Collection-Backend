package dev.cheerfun.pixivic.biz.cibr.service;

import com.google.gson.JsonObject;
import dev.cheerfun.pixivic.biz.cibr.constant.MilvusInfo;
import dev.cheerfun.pixivic.common.exception.BaseException;
import io.milvus.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/6 11:02 PM
 * @description MilvusService
 */
//@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MilvusService {
    private final MilvusClient client;

    @PostConstruct
    public void init() {
        String collectionName = MilvusInfo.COLLECTION;
        //client.flush(collectionName);
        //client.dropCollection(collectionName);
        if (!hasCollection(collectionName)) {
            createCollection(collectionName, 512, 2048);
            createIndexForCollection(collectionName, IndexType.IVFLAT, 4500 * 4/*145*4*/);
        }
        log.info("当前集合内实体总数为：" + client.countEntities(collectionName).getCollectionEntityCount());
        Response getCollectionStatsResponse = client.getCollectionStats(collectionName);
        if (getCollectionStatsResponse.ok()) {
            // Collection info is sent back with JSON type string
            String jsonString = getCollectionStatsResponse.getMessage();
            System.out.format("Collection Stats: %s\n", jsonString);
            GetIndexInfoResponse getIndexInfoResponse = client.getIndexInfo(collectionName);
            System.out.format("Index Info: %s\n", getIndexInfoResponse.getIndex().get().toString());
        }
        ListPartitionsResponse listPartitionsResponse = client.listPartitions(collectionName);
        log.info(listPartitionsResponse.getPartitionList());

    }

    public Boolean hasCollection(String collectionName) {
        return client.hasCollection(collectionName).hasCollection();
    }

    public Boolean createCollection(String collectionName, Integer dimension, Integer indexFileSize) {
        final MetricType metricType = MetricType.L2;
        CollectionMapping collectionMapping =
                new CollectionMapping.Builder(collectionName, dimension)
                        .withIndexFileSize(indexFileSize)
                        .withMetricType(metricType)
                        .build();
        Response createCollectionResponse = client.createCollection(collectionMapping);
        return createCollectionResponse.ok();
    }

    public void createIndexForCollection(String collectionName, IndexType indexType, Integer nlist) {
        JsonObject indexParamsJson = new JsonObject();
        indexParamsJson.addProperty("nlist", nlist);
        Index index =
                new Index.Builder(collectionName, indexType)
                        //   .withParamsInJson(indexParamsJson.toString())
                        .build();
        Response createIndexResponse = client.createIndex(index);
    }

    public void flush(String collectionName) {
        client.flush(collectionName);
    }

    public List<Integer> search(List<Float> vector, Integer k) {
        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", 256);
        SearchParam searchParam =
                new SearchParam.Builder(MilvusInfo.COLLECTION)
                        .withFloatVectors(Collections.singletonList(normalizeVector(vector)))
                        .withTopK(k)
                        .withParamsInJson(searchParamsJson.toString())
                        .build();
        SearchResponse searchResponse = client.search(searchParam);
        if (searchResponse.ok()) {
            List<List<SearchResponse.QueryResult>> queryResultsList =
                    searchResponse.getQueryResultsList();
            List<SearchResponse.QueryResult> queryResults = queryResultsList.get(0);
            return queryResults.stream().mapToInt(e -> Math.toIntExact(e.getVectorId())).boxed().collect(Collectors.toList());
        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST, "以图搜图出错");
        }
    }

    //将特征向量存入Milvus
    public Boolean saveIllustFeatureToMilvus(Long illustId, List<List<Float>> imageFeatureList, String collectionName) {
        imageFeatureList =
                imageFeatureList.stream().map(this::normalizeVector).collect(Collectors.toList());
        InsertParam insertParam =
                new InsertParam.Builder(collectionName).withFloatVectors(imageFeatureList).withVectorIds(Collections.nCopies(imageFeatureList.size(), illustId)).build();
        client.insert(insertParam);
        return true;
    }

    private List<Float> normalizeVector(List<Float> vector) {
        float squareSum = vector.stream().map(x -> x * x).reduce((float) 0, Float::sum);
        final float norm = (float) Math.sqrt(squareSum);
        vector = vector.stream().map(x -> x / norm).collect(Collectors.toList());
        return vector;
    }

}
