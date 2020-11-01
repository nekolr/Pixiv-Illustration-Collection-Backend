package dev.cheerfun.pixivic.biz.web.sentence.service;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import dev.cheerfun.pixivic.biz.web.sentence.mapper.SentenceMapper;
import dev.cheerfun.pixivic.biz.web.sentence.po.Sentence;
import lombok.RequiredArgsConstructor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/30 9:19 PM
 * @description SentenceService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SentenceService {
    private final ObjectMapper objectMapper;
    private final SentenceMapper sentenceMapper;
    final private HttpClient httpClient;

    //@PostConstruct
    public void initiate() throws IOException {
        File json = new File("/Users/oysterqaq/Desktop/sentences-bundle-master/sentences/c.json");
        List<Sentence> sentences = objectMapper.readValue(json, new TypeReference<ArrayList<Sentence>>() {
        });
        sentences.forEach(e -> sentenceMapper.insertSentence(e));

    }

    //爬取juzikong
    //@PostConstruct
    public void crawlerA() throws IOException, InterruptedException {
        //列表循环 https://www.juzikong.com/categories/works/animes?page=1 1到45
     /*   List<String> strings = Files.readLines(new File("/Users/oysterqaq/Desktop/a.txt"), Charset.defaultCharset());

        objectMapper.writeValue(new File("/Users/oysterqaq/Desktop/a.txt"),strings);*/
        List<String> strings = objectMapper.readValue(new File("/Users/oysterqaq/Desktop/a.txt"), new TypeReference<ArrayList<String>>() {
        });

        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).contains("##")) {
                continue;
            }
            String body = null;
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://www.juzikong.com" + strings.get(i)))
                        .header("User-Agent", "PostmanRuntime/7.26.5")
                        .header("Accept", "*/*")
                        .GET().build();
                body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
                Document doc = Jsoup.parse(body);
                String title = doc.getElementsByClass("name_xkKmN").get(0).text().replace("《", "").replace("》", "");
                Elements number = doc.getElementsByClass("number");
                Integer sum = 0;
                if (number != null && number.size() > 0) {
                    sum = Integer.valueOf(doc.getElementsByClass("number").last().child(0).text());
                }
                strings.set(i, strings.get(i) + "##" + title + "##" + sum);
            } catch (Exception e) {
                System.out.println(body);
                System.out.println(i);
                e.printStackTrace();
                objectMapper.writeValue(new File("/Users/oysterqaq/Desktop/a.txt"), strings);
                break;
            }
            objectMapper.writeValue(new File("/Users/oysterqaq/Desktop/a.txt"), strings);
            Thread.sleep(1000 * 10);
        }

//        for (int i = 1; i <46; i++) {
//            try {
//                HttpRequest request = HttpRequest.newBuilder()
//                        .uri(URI.create("https://www.juzikong.com/categories/works/animes?page="+i))
//                        .header("User-Agent","PostmanRuntime/7.26.5")
//                        .header("Accept","*/*")
//                        .GET().build();
//                String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
//                Document doc = Jsoup.parse(body);
//                Elements elements = doc.getElementsByClass("title_28-Lx");
//                elements.stream().map(e->e.child(0).attr("href")).collect(Collectors.toList()).forEach(System.out::println);
//            }catch (Exception e){
//                System.out.println(i);
//                e.printStackTrace();
//                return;
//            }
//
//        }
        //找到
    }
}
