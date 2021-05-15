package dev.cheerfun.pixivic.biz.web.cbir.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/15 7:41 PM
 * @description ImageReverseSearchService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImageReverseSearchService {

    private final IllustrationBizService illustrationBizService;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    @Value("${cbir.server}")
    private String cbirServer;

    @Cacheable("cbir")
    public List<Illustration> queryTopKSimilarImage(String imageUrl) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(cbirServer + "/similarImages?imageUrl=" + URLEncoder.encode(imageUrl)))
                .GET()
                .build();
        String body = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        Result<List<Illustration>> result = objectMapper.readValue(body, new TypeReference<Result<List<Illustration>>>() {
        });
        return result.getData();
    }

    @Cacheable("generateTag")
    public List<String> generateTagForImage(String imageUrl) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(cbirServer + "/imageTags?imageUrl=" + URLEncoder.encode(imageUrl)))
                .GET()
                .build();
        String body = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        Result<List<String>> result = objectMapper.readValue(body, new TypeReference<Result<List<String>>>() {
        });
        return result.getData();
    }
}
