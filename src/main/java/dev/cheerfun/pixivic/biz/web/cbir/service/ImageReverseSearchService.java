package dev.cheerfun.pixivic.biz.web.cbir.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.cbir.secmapper.CBIRSyncMapper;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final CBIRSyncMapper cbirSyncMapper;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    @Value("${cbir.server}")
    private String cbirServer;

    public List<Illustration> queryTopKSimilarImage(String imageUrl) throws IOException, InterruptedException {
        return illustrationBizService.queryIllustrationByIdList(queryTopKIdSimilarImage(imageUrl));
    }

    @Cacheable("cbir")
    public List<Integer> queryTopKIdSimilarImage(String imageUrl) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(cbirServer + "/similarImages?imageUrl=" + URLEncoder.encode(imageUrl)))
                .GET()
                .build();
        String body = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        Result<List<Integer>> result = objectMapper.readValue(body, new TypeReference<Result<List<Integer>>>() {
        });
        if (result.getData() != null) {
            return result.getData();
        }
        return new ArrayList<>();
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

    //@Scheduled(cron = "0 30 0 * * ?")
    @Scheduled(cron = "0 0 2 * * MON,WED,FRI")
    //TODO ???????????????????????????????????????
    public void dailySyncTask() {
        syncToCBIRServer(null);
    }

    //?????????????????????cbirServer
    public Boolean syncToCBIRServer(String date) {
        String toDate = null;
        if (date == null) {
            date = LocalDate.now().plusDays(-3).toString();
            toDate = LocalDate.now().toString();
        } else {
            toDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(+1).toString();
        }
        log.info("???????????? " + date + " ??????????????????????????????");
        //??????updateTime??????????????????
        try {
            List<Integer> illustIdList = cbirSyncMapper.queryForSync(date, toDate);
            List<Illustration> illustrations = illustrationBizService.queryIllustrationByIdList(illustIdList);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(cbirServer + "/syncIllustsInfo"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            objectMapper.writeValueAsString(illustrations)))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 504) {
                //?????????????????????
                cbirSyncMapper.updateSyncLog(date, 1);
                log.info("?????? " + date + " ????????????????????????????????????");
                return true;
            } else {
                cbirSyncMapper.updateSyncLog(date, 0);
                log.error("?????? " + date + " ????????????????????????????????????");
                log.error(response.statusCode() + "\n" + response.body());
            }
        } catch (Exception e) {
            log.error("?????? " + date + " ????????????????????????????????????");
            e.printStackTrace();
            cbirSyncMapper.updateSyncLog(date, 0);
        }
        return false;
    }

}
