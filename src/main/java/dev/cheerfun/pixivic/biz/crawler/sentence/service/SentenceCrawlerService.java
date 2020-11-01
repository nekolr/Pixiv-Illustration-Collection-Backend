package dev.cheerfun.pixivic.biz.crawler.sentence.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/30 10:41 PM
 * @description SentenceCrawlerService
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SentenceCrawlerService {
    final private HttpClient httpClient;
    final private ObjectMapper objectMapper;

    //爬取juzikong
    //@PostConstruct
    public void crawlerA() throws IOException, InterruptedException {
        //列表循环 https://www.juzikong.com/categories/works/animes?page=1 1到45

        for (int i = 1; i < 46; i++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://www.juzikong.com/categories/works/animes?page=" + i)).GET().build();
                String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
                Document doc = Jsoup.parse(body);
                Elements elements = doc.getElementsByClass("title_28-Lx");
                elements.stream().map(e -> e.child(0).attr("href")).collect(Collectors.toList()).forEach(System.out::println);
            } catch (Exception e) {
                System.out.println(i);
                e.printStackTrace();
                return;
            }

        }
        //找到
    }

    //爬取mingyantong
}
