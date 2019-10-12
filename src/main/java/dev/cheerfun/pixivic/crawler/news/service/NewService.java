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
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
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

    @Scheduled(cron = "0 1 0 * * ?")
    public void dailyPullTask() throws IOException, InterruptedException {
        pullDMZJNews();

    }

    private void pullDMZJNews() throws IOException, InterruptedException {
        // 获取列表
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://v3api.dmzj.com/v3/article/list/1/3/0.json?channel=ios&version=1.0.2")).GET().build();
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        List<DMZJNewDTO> dmzjNewDTOList = objectMapper.readValue(body, new TypeReference<List<DMZJNewDTO>>() {
        });
        //查询未在数据库中的
        Set<String> titleList = dmzjNewDTOList.stream().map(DMZJNewDTO::getTitle).collect(Collectors.toSet());
        Set<String> finalTitleList = newMapper.queryNewsNotInDb(titleList);
        //进行抓取实际文章
        List<ACGNew> acgNewList = dmzjNewDTOList.stream()
                .filter(d -> finalTitleList.contains(d.getTitle()))
                .map(d -> {
                    HttpRequest r = HttpRequest.newBuilder()
                            .uri(URI.create(d.getRefererUrl())).GET().build();
                    ACGNew acgNew = null;
                    try {
                        String b = httpClient.send(r, HttpResponse.BodyHandlers.ofString()).body();
                        //提取元素
                        Document doc = Jsoup.parse(b);
                        Elements newsBox = doc.getElementsByClass("news_box");
                        //替换图片
                        acgNew = new ACGNew(d.getTitle(), d.getIntro(), d.getAuthor(), d.getCover(), d.getRefererUrl(), newsBox.html(), new Date(d.getCreateTime()), "动漫之家");
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return acgNew;
                }).collect(Collectors.toList());
        //处理后存入数据库
        newMapper.insert(acgNewList);
    }

    private void pullACGMHNews() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.acgmh.com/wp-admin/admin-ajax.php?action=zrz_load_more_posts")).POST(HttpRequest.BodyPublishers.ofString("type=catL3&paged=1")).build();
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        ACGMHNewsDTO acgmhNewsDTO = objectMapper.readValue(body, ACGMHNewsDTO.class);
        Document doc = Jsoup.parse(acgmhNewsDTO.getMsg().replace("\\\"", "\""));
        Elements elements = doc.getElementsByClass("pos-r pd10 post-list box mar10-b content");
        //elements.
    }

}
