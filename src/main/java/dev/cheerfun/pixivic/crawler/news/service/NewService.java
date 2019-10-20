package dev.cheerfun.pixivic.crawler.news.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.common.model.ACGNew;
import dev.cheerfun.pixivic.crawler.news.dto.ACGMHNewsDTO;
import dev.cheerfun.pixivic.crawler.news.dto.DMZJNewDTO;
import dev.cheerfun.pixivic.crawler.news.mapper.NewMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/10/12 10:31
 * @description NewService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NewService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final NewMapper newMapper;
    private static final String DMZJ = "动漫之家";
    private static final String ACGMH = "ACG门户";
    private static final String ACG17 = "ACG在一起";
    private static final String LOLIHY = "萝莉花园";
    private static final String FUTA404 = "扶她404";
    //DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Scheduled(cron = "0 30 0 * * ?")
    public void dailyPullTask() throws IOException, InterruptedException {
        pullDMZJNews();
        pullACGMHNews();
        pullACG17News();
        pullFUTA404News();
    }

    private void pullDMZJNews() throws IOException, InterruptedException {
        // 获取列表
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://v3api.dmzj.com/v3/article/list/1/3/0.json?channel=ios&version=1.0.2")).GET().build();
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        List<DMZJNewDTO> dmzjNewDTOList = objectMapper.readValue(body, new TypeReference<List<DMZJNewDTO>>() {
        });
        List<ACGNew> acgNewList = dmzjNewDTOList.stream().map(DMZJNewDTO::cast).collect(Collectors.toList());
        process(acgNewList, "class", "news_box");
    }

    private void pullACGMHNews() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.acgmh.com/category/news")).POST(HttpRequest.BodyPublishers.ofString("type=catL3&paged=1")).build();
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        //ACGMHNewsDTO acgmhNewsDTO = objectMapper.readValue(body, ACGMHNewsDTO.class);
        Document doc = Jsoup.parse(body);
        Elements elements = doc.getElementsByClass("pos-r pd10 post-list box mar10-b content");
        List<ACGNew> acgNewList = elements.stream().map(e -> {
            String style = e.getElementsByClass("preview thumb-in").get(0).attr("style");
            String cover = style.substring(style.indexOf("('") + 2, style.length() - 2);
            String author = e.getElementsByClass("users").text();
            String createDate = e.getElementsByClass("timeago").text();
            Elements es = e.getElementsByClass("entry-title");
            String title = es.text();
            String refererUrl = es.get(0).getElementsByTag("a").get(0).attr("href");
            String intro = e.getElementsByClass("mar10-b post-ex mar10-t mobile-hide").text();
            return new ACGNew(title, intro, author, cover, refererUrl, LocalDate.parse(createDate.substring(0,10)), ACGMH);
        }).collect(Collectors.toList());
        process(acgNewList, "id", "content-innerText");
    }

    private void pullACG17News() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://acg17.com/category/news/")).GET().build();
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        Document doc = Jsoup.parse(body);
        Elements elements = doc.getElementsByClass("item-list");
        List<ACGNew> acgNewList = elements.stream().map(e -> {
            String style = e.getElementsByClass("attachment-tie-medium size-tie-medium wp-post-image").get(0).attr("style");
            String cover = style.substring(style.indexOf("url(") + 4, style.indexOf(")"));
            Element t = e.getElementsByClass("post-box-title").get(0).child(0);
            LocalDate createDate = LocalDate.parse(e.getElementsByClass("tie-date").get(0).text().replaceAll("[年月]","-").replace("日",""));
            String intro = e.getElementsByClass("entry").get(0).child(0).text();
            String title = t.text();
            String rerfererUrl = t.attr("href");
            return new ACGNew(title, intro, ACG17, cover, rerfererUrl, createDate, ACG17);
        }).collect(Collectors.toList());
        process(acgNewList, "class", "entry");
    }

    private void pullFUTA404News() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://www.futa404.com/category/dmaz")).GET().build();
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        Document doc = Jsoup.parse(body);
        Elements elements = doc.getElementsByClass("card flex-fill mb-4 mb-sm-4-2 mb-md-4 mb-lg-4-2");
        List<ACGNew> acgNewList = elements.stream().map(e -> {
            String cover = e.getElementsByClass("lazyload custom-hover-img original").get(0).attr("src");
            Element t = e.getElementsByClass("custom-hover d-block").get(0);
            String title = t.attr("title");
            String rerfererUrl = t.attr("href");
            String time = e.getElementsByClass("u-time").get(0).text();
            LocalDate createDate;
            if (time.contains("-")) {
                createDate = LocalDate.parse(time);
            } else {
                createDate = LocalDate.now();
            }
            String intro = e.getElementsByClass("text-l2 font-md-12 text-secondary").get(0).text();
            return new ACGNew(title, intro, FUTA404, cover, rerfererUrl, createDate, FUTA404);
        }).collect(Collectors.toList());
        process(acgNewList, "class", "post-content suxing-popup-gallery");
    }

    private void process(List<ACGNew> acgNewList, String type, String name) {
        //查询未在数据库中的
        if (acgNewList != null && acgNewList.size() > 0) {
            List<String> titleList = acgNewList.stream().map(ACGNew::getTitle).collect(Collectors.toList());
            Set<String> finalTitleList = new HashSet<>(newMapper.queryNewsNotInDb(titleList));
            //进行抓取实际文章
            if (finalTitleList.size() > 0) {
                acgNewList.stream()
                        .filter(d -> finalTitleList.contains(d.getTitle())).forEach(n -> {
                    HttpRequest r = HttpRequest.newBuilder()
                            .uri(URI.create(n.getRefererUrl())).GET().build();
                    try {
                        String b = httpClient.send(r, HttpResponse.BodyHandlers.ofString()).body();
                        Document d = Jsoup.parse(b);
                        if ("class".equals(type)) {
                            n.setContent(d.getElementsByClass(name).get(0).html());
                        } else {
                            n.setContent(d.getElementById(name).html());
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                newMapper.insert(acgNewList);
            }
        }

    }

}
