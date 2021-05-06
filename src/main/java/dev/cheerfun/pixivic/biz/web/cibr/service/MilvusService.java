package dev.cheerfun.pixivic.biz.web.cibr.service;

import com.google.gson.JsonObject;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import io.milvus.client.MilvusClient;
import io.milvus.client.SearchParam;
import io.milvus.client.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/6 11:02 PM
 * @description MilvusService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MilvusService {
    private final MilvusClient client;

    public List<Integer> search(List<Float> vector, Integer k) {
        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", 20);
        SearchParam searchParam =
                new SearchParam.Builder("collectionName")
                        .withFloatVectors(Collections.singletonList(vector))
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
            throw new BusinessException(HttpStatus.BAD_REQUEST, "以图搜图出错");
        }
    }

}
