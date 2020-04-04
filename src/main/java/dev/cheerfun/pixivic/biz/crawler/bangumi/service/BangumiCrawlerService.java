package dev.cheerfun.pixivic.biz.crawler.bangumi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/4 11:06 下午
 * @description BangumiCrawlerService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BangumiCrawlerService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void pullAllAnimateInfo() throws IOException, InterruptedException {
        final int endPageNum = 670;
        final Path path = Paths.get("/Users/oysterqaq/Desktop/id.txt");
        //从redis中查出上次爬到哪一页
        String subject_id_list = stringRedisTemplate.opsForValue().get("subject_id_list");
        int currentIndex;
        if (subject_id_list != null) {
            currentIndex = Integer.parseInt(subject_id_list);
        } else {
            currentIndex = 0;
        }
        //开始查找id并添加到文件
        for (; currentIndex < endPageNum; currentIndex++) {
            System.out.println("开始爬取第" + currentIndex + "页");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://bangumi.tv/anime/browser/?sort=date&page=" + currentIndex)).GET().build();
            String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            //jsoup提取文本
            Document doc = Jsoup.parse(body);
            Elements elements = doc.getElementsByClass("subjectCover cover ll");
            elements.forEach(e -> {
                try {
                    String href = e.attr("href").replaceAll("\\D", "") + "\n";
                    System.out.println(href);
                    Files.writeString(path, href, StandardOpenOption.APPEND);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            stringRedisTemplate.opsForValue().set("subject_id_list", String.valueOf(currentIndex));
        }
    }
}
