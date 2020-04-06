package dev.cheerfun.pixivic.biz.crawler.bangumi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.bangumi.domain.Animate;
import dev.cheerfun.pixivic.biz.crawler.bangumi.domain.AnimateCharacter;
import dev.cheerfun.pixivic.biz.crawler.bangumi.domain.Seiyuu;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    //@PostConstruct
    public void pullAllAnimateInfo() throws IOException, InterruptedException {
//读取idtxt获取id进行采集
        List<String> strings = Files.readAllLines(Paths.get("/Users/oysterqaq/Desktop/id.txt"));
        for (int i = 15000; i < 15010; i++) {
            try {
                System.out.println(pullAnimateInfo(Integer.valueOf(strings.get(i))));
            } catch (Exception e) {
                System.out.println(i + "失败");
                e.printStackTrace();
            }

        }

    }

    private List<Integer> querySubjectId(Integer pageNum) throws IOException, InterruptedException {
        List<Integer> idList = new ArrayList<>(24);
        int currentIndex = 0;
        //开始查找id并添加到文件
        for (; currentIndex < pageNum; currentIndex++) {
            System.out.println("开始爬取第" + currentIndex + "页");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://bangumi.tv/anime/browser/?sort=date&page=" + currentIndex)).GET().build();
            String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            //jsoup提取文本
            Document doc = Jsoup.parse(body);
            Elements elements = doc.getElementsByClass("subjectCover cover ll");
            elements.forEach(e -> {
                idList.add(Integer.parseInt(e.attr("href").replaceAll("\\D", "") + "\n"));
            });
        }
        return idList;
    }

    private Animate pullAnimateInfo(Integer subjectId) throws IOException, InterruptedException {
        Animate animate = new Animate();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://bangumi.tv/subject/" + subjectId))
                .header("Cookie", "chii_theme=light; __utmc=1; chii_cookietime=0; chii_auth=7f8gLTYkeoBbe1CGYReErTifr1BNqPNs3YRVoCSSdGqaJASiuAbjjrsm4jnNmmy0U2ldwOfAnP9CXxlQsX98LyJv36U6kqrNFl5Y; __utmz=1.1585994994.3.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); chii_sid=ypnNkh; __utma=1.220230553.1585409845.1586173745.1586178721.8; __utmt=1; __utmb=1.4.10.1586178721").GET().build();
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        Document doc = Jsoup.parse(body);
        //获取角色
        HttpRequest pullCharacters = HttpRequest.newBuilder()
                .uri(URI.create("https://bangumi.tv/subject/" + subjectId + "/characters"))
                .header("Cookie", "chii_theme=light; __utmc=1; chii_cookietime=0; chii_auth=7f8gLTYkeoBbe1CGYReErTifr1BNqPNs3YRVoCSSdGqaJASiuAbjjrsm4jnNmmy0U2ldwOfAnP9CXxlQsX98LyJv36U6kqrNFl5Y; __utmz=1.1585994994.3.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); chii_sid=ypnNkh; __utma=1.220230553.1585409845.1586173745.1586178721.8; __utmt=1; __utmb=1.4.10.1586178721")
                .GET().build();
        String charactersBody = httpClient.send(pullCharacters, HttpResponse.BodyHandlers.ofString()).body();
        Document charactersDoc = Jsoup.parse(charactersBody);
        Element columnInSubjectA = charactersDoc.getElementById("columnInSubjectA");
        Elements light_odd = columnInSubjectA.getElementsByClass("light_odd");
        animate.setCharacters(light_odd.stream().map(e -> {
            AnimateCharacter animateCharacter = new AnimateCharacter();
            Elements avatar = e.getElementsByClass("tip icons_cmt");
            if (avatar != null && avatar.size() > 0) {
                try {
                    animateCharacter.setId(Integer.valueOf(avatar.get(0).attr("href").replace("/character/", "")));
                } catch (Exception e1) {
                }
            }
            animateCharacter.setAvatar("https:" + e.getElementsByClass("avatar ll").get(0).attr("src"));
            Element clearit = e.getElementsByClass("clearit").get(0);
            Element h2 = clearit.child(0);
            animateCharacter.setName(h2.child(0).text());
            Elements tip = h2.getElementsByClass("tip");
            if (tip != null && tip.size() > 0)
                animateCharacter.setTranslatedName(h2.getElementsByClass("tip").get(0).text().replace("/ ", ""));
            Element crt_info = clearit.child(1);
            animateCharacter.setType(crt_info.child(0).text());
            animateCharacter.setDetail(crt_info.child(1).text());
            Elements actorBadge_clearit = clearit.getElementsByClass("actorBadge clearit");
            if (actorBadge_clearit != null && actorBadge_clearit.size() > 0) {
                Element element = actorBadge_clearit.get(0);
                Seiyuu seiyuu = new Seiyuu();
                seiyuu.setAvatar("https:" + element.getElementsByClass("avatar ll").attr("src"));
                seiyuu.setId(Integer.valueOf(element.child(0).attr("href").replace("/person/", "")));
                seiyuu.setName(element.getElementsByClass("l").get(0).text());
                seiyuu.setTranslatedName(element.getElementsByClass("grey").get(0).text());
                animateCharacter.setSeiyuu(seiyuu);
            }

            return animateCharacter;
        }).collect(Collectors.toList()));
        Element nameSingle = doc.getElementsByClass("nameSingle").get(0);

        Element title = nameSingle.child(0);
        Elements thickbox_cover = doc.getElementsByClass("thickbox cover");
        if (thickbox_cover != null && thickbox_cover.size() > 0) {
            animate.setCover("https:" + thickbox_cover.get(0).attr("href"));
        }
        if (nameSingle.getElementsByClass("grey") != null) {
            animate.setType(nameSingle.getElementsByClass("grey").text());

        }
        animate.setId(subjectId);
        animate.setTitle(title.text());
        animate.setTranslatedTitle(title.attr("title"));
        if (doc.getElementById("subject_summary") != null) {
            animate.setIntro(doc.getElementById("subject_summary").text());
        }
        Elements subject_tag_section = doc.getElementsByClass("subject_tag_section");
        if (subject_tag_section != null && subject_tag_section.size() > 0) {
            Elements tags = subject_tag_section.get(0).getElementsByClass("inner").get(0).getElementsByTag("a");
            animate.setTags(tags.stream().map(t -> t.child(0).text()).collect(Collectors.toList()));
        }
        animate.setRate(Float.valueOf(doc.getElementsByClass("global_score").get(0).child(0).text()));
        Elements infobox = doc.getElementById("infobox").getElementsByTag("li");
        HashMap<String, String> detail = new HashMap<>();
        infobox
                .forEach(
                        e -> {
                            String key = e.getElementsByClass("tip").text().replace(":", "");
                            String text = e.text();
                            String value = text.substring(text.indexOf(": ") + 2);
                            detail.merge(key, value, (a, b) -> a + "|" + b);
                        });
        animate.setDetail(detail);

        return animate;
    }

}
