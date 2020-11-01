package dev.cheerfun.pixivic.biz.web.sentence.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.sentence.mapper.SentenceMapper;
import dev.cheerfun.pixivic.biz.web.sentence.po.Sentence;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

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

            if (!strings.get(i).contains("##")) {
                continue;
            }
            String[] split = strings.get(i).split("##");
            Integer sum = Integer.valueOf(split[2]);
            if (sum.compareTo(0) == 0) {
                sum++;
            }

            String title = split[1];
            String url = split[0];
            String body = null;
            try {

                for (int j = 1; j <= sum; j++) {

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://www.juzikong.com" + url + "?page=" + j))
                            .header("User-Agent", "PostmanRuntime/7.26.5")
                            .header("Accept", "*/*")
                            .GET().build();

                    body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
                    if (body.contains("Your IP")) {
                        throw new EOFException();
                    }
                    Document doc = Jsoup.parse(body);
                    Elements elements = doc.getElementsByClass("content_2hYZM");
                    for (Element element : elements) {
                        List<String> textNodes = element.child(0).children().eachText();
                        StringBuilder content = new StringBuilder();
                        textNodes.forEach(e -> content.append(e + "\n"));
                        String fromWho = null;
                        Elements elementsByClass = element.getElementsByClass("link-wrapper_mbSHZ");
                        if (elementsByClass != null && elementsByClass.size() > 0) {
                            fromWho = elementsByClass.get(0).getElementsByTag("a").get(0).text();
                        }
                        sentenceMapper.insertSentence(new Sentence(null, content.deleteCharAt(content.length() - 1).toString(), title, fromWho));
                    }
                    Thread.sleep(1000 * 5);
                }
                strings.set(i, url);
                objectMapper.writeValue(new File("/Users/oysterqaq/Desktop/a.txt"), strings);
            } catch (Exception e) {
                System.out.println(body);
                System.out.println(i);
                e.printStackTrace();
                break;
            }

        }

    }
}
