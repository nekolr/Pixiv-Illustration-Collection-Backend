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

    @Cacheable("cbir")
    public List<Illustration> queryTopKSimilarImage(String imageUrl) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(cbirServer + "/similarImages?imageUrl=" + URLEncoder.encode(imageUrl)))
                .GET()
                .build();
        String body = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        Result<List<Integer>> result = objectMapper.readValue(body, new TypeReference<Result<List<Integer>>>() {
        });
        return illustrationBizService.queryIllustrationByIdList(result.getData());
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
    //TODO 和推荐同步（此时机器空闲）
    public void DailySyncTask() {
        syncToCBIRServer(null);
    }

    //每日同步画作到cbirServer
    public Boolean syncToCBIRServer(String date) {
        String toDate = null;
        if (date == null) {
            date = LocalDate.now().plusDays(-3).toString();
            toDate = LocalDate.now().toString();
        } else {
            toDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(+1).toString();
        }
        log.info("开始同步 " + date + " 画作到以图搜图服务器");
        //找到updateTime在当天的画作
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
                //更新数据库记录
                cbirSyncMapper.updateSyncLog(date, 1);
                log.info("同步 " + date + " 画作到以图搜图服务器成功");
                return true;
            } else {
                cbirSyncMapper.updateSyncLog(date, 0);
                log.error("同步 " + date + " 画作到以图搜图服务器失败");
                log.error(response.statusCode() + "\n" + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            cbirSyncMapper.updateSyncLog(date, 0);
        }
        return false;
    }

}
