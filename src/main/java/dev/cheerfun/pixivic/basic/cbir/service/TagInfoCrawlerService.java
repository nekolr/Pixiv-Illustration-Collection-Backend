package dev.cheerfun.pixivic.basic.cbir.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.basic.cbir.domain.FeatureTag;
import dev.cheerfun.pixivic.basic.cbir.mapper.FeatureTagMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mortbay.util.UrlEncoded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/9/23 5:52 PM
 * @description TagInfoCrawlerService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TagInfoCrawlerService {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final FeatureTagMapper featureTagMapper;

    //@PostConstruct
    public void dael() {
        List<FeatureTag> featureTags = queryAllTag();
        featureTags.forEach(e ->
                {
                    try {
                        dealEach(e);
                    } catch (IOException | InterruptedException ioException) {
                        ioException.printStackTrace();
                    }
                }
        );
    }

    @Transactional
    private void dealEach(FeatureTag featureTag) throws IOException, InterruptedException {
        String document = getDocument(featureTag.getContent());
        featureTag = castToFeatureTag(document, featureTag);
        update(featureTag);
    }

    //查找出所有tag
    private List<FeatureTag> queryAllTag() {
        return featureTagMapper.queryAll();
    }

    //根据tag内容拼接url获取网页
    private String getDocument(String keyWord) throws IOException, InterruptedException {
        URI uri = null;
        try {
            uri = URI.create("https://danbooru.donmai.us/wiki_pages/" + URLEncoder.encode(keyWord));
        } catch (Exception e) {
            System.out.println("非法url：" + "https://danbooru.donmai.us/wiki_pages/" + keyWord);
            return "";
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri).GET().build();
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        return body;
    }

    //jsoup解析
    //构造出featureTag
    private FeatureTag castToFeatureTag(String document, FeatureTag featureTag) throws JsonProcessingException {
        Document doc = Jsoup.parse(document);
        Elements elements = doc.getElementsByClass("wiki-other-name");
        if (elements != null && elements.size() > 0) {
            List<String> tagList = elements.stream().map(Element::text).collect(Collectors.toList());
            featureTag.setPixivTags(objectMapper.writeValueAsString(tagList));
        }
        StringBuilder stringBuilder = new StringBuilder();
        Element b = doc.getElementById("wiki-page-body");
        if (b != null) {
            Elements allElements = b.children();
            if (allElements != null) {
                for (int i = 0; i < allElements.size(); i++) {
                    Element element = allElements.get(i);
                    if (element.children().stream().anyMatch(e -> "wiki-other-name".equals(element.className()))) {
                        continue;
                    }
                    if ("p".equals(element.tag().getName())) {
                        stringBuilder.append(element.text());
                        stringBuilder.append("<br>");
                    } else {
                        break;
                    }
                }
            }
            featureTag.setDesc(stringBuilder.toString());
            featureTag.setExtendContent(b.html());
        }
        if (featureTag.getExtendContent() == null) {
            featureTag.setExtendContent("");
        }

        return featureTag;
    }

    //根据id更新
    private Boolean update(FeatureTag featureTag) {
        featureTagMapper.updateById(featureTag);
        return true;
    }

}
