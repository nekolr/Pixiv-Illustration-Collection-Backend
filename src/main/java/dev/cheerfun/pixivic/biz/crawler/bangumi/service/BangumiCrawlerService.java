package dev.cheerfun.pixivic.biz.crawler.bangumi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.bangumi.domain.Animate;
import dev.cheerfun.pixivic.biz.crawler.bangumi.domain.AnimateCharacter;
import dev.cheerfun.pixivic.biz.crawler.bangumi.domain.Seiyuu;
import dev.cheerfun.pixivic.biz.crawler.bangumi.secmapper.AnimateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/4 11:06 下午
 * @description BangumiCrawlerService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BangumiCrawlerService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final AnimateMapper animateMapper;

    //@PostConstruct
    public void pullAllAnimateInfo() throws IOException, InterruptedException {
        //读取idtxt获取id进行采集
        List<String> strings = Files.readAllLines(Paths.get("/home/PIC/id.txt"));
        //获取读取到了第几行
        int index = 0;
        String animate = stringRedisTemplate.opsForValue().get("animate");
        if (animate != null) {
            index = Integer.parseInt(animate);
        }
        for (int i = index; i < 15864; i++) {
            log.info("开始爬取第" + i + "个");
            List<Animate> animates = new ArrayList<>(1);
            animates.add(pullAnimateInfo(Integer.valueOf(strings.get(i))));
            animateMapper.insertAnimateList(animates);
            List<AnimateCharacter> animateCharacterList = animates.stream().map(Animate::getCharacters).flatMap(Collection::stream).filter(e -> e.getId() != null).collect(Collectors.toList());
            if (animateCharacterList.size() > 0) {
                animateMapper.insertCharacterList(animateCharacterList, Integer.valueOf(strings.get(i)));
                List<Seiyuu> seiyuuList = animateCharacterList.stream().filter(e -> e.getSeiyuu() != null && e.getSeiyuu().getId() != null).map(AnimateCharacter::getSeiyuu).collect(Collectors.toList());
                if (seiyuuList.size() > 0) {
                    animateMapper.insertSeiyuuList(seiyuuList);
                }
            }
            stringRedisTemplate.opsForValue().set("animate", String.valueOf(i + 1));

        }

    }

    private List<Integer> querySubjectId(Integer pageNum) throws IOException, InterruptedException {
        List<Integer> idList = new ArrayList<>(24);
        int currentIndex = 0;
        //开始查找id并添加到文件
        for (; currentIndex < pageNum; currentIndex++) {
            log.info("开始爬取第" + currentIndex + "页");
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
        animate.setId(subjectId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://bangumi.tv/subject/" + subjectId))
                .header("Cookie", "chii_theme=light; __utmc=1; chii_cookietime=0; chii_sid=7EnC27; __utma=1.220230553.1585409845.1586178721.1586276424.9; __utmz=1.1586276424.9.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); __utmt=1; chii_auth=6UpwAF9Kw56gzt9HcRW08BWFX7Xh5e0ujMBdyayWlBIudqEIkqnBfdMXjmwtZ6vf9fC9Awfeqa5iDfOja4xGV2PqdQ70AJphq6W2; __utmb=1.5.10.158627642").GET().build();
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        Document doc = Jsoup.parse(body);
        //获取角色
        HttpRequest pullCharacters = HttpRequest.newBuilder()
                .uri(URI.create("https://bangumi.tv/subject/" + subjectId + "/characters"))
                .header("Cookie", "chii_theme=light; __utmc=1; chii_cookietime=0; chii_sid=7EnC27; __utma=1.220230553.1585409845.1586178721.1586276424.9; __utmz=1.1586276424.9.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); __utmt=1; chii_auth=6UpwAF9Kw56gzt9HcRW08BWFX7Xh5e0ujMBdyayWlBIudqEIkqnBfdMXjmwtZ6vf9fC9Awfeqa5iDfOja4xGV2PqdQ70AJphq6W2; __utmb=1.5.10.158627642")
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
        Elements nameSingle1 = doc.getElementsByClass("nameSingle");
        if (nameSingle1.size() > 0) {
            Element nameSingle = nameSingle1.get(0);

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
        }

        if (doc.getElementById("subject_summary") != null) {
            animate.setIntro(doc.getElementById("subject_summary").text());
        }
        Elements subject_tag_section = doc.getElementsByClass("subject_tag_section");
        if (subject_tag_section != null && subject_tag_section.size() > 0) {
            Elements tags = subject_tag_section.get(0).getElementsByClass("inner").get(0).getElementsByTag("a");
            animate.setTags(tags.stream().map(t -> t.child(0).text()).collect(Collectors.toList()));
        }
        Elements global_score = doc.getElementsByClass("global_score");
        if (global_score.size() > 0) {
            animate.setRate(Float.valueOf(global_score.get(0).child(0).text()));
        }
        Element infobox1 = doc.getElementById("infobox");
        if (infobox1 != null) {
            Elements infobox = infobox1.getElementsByTag("li");
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
        }

        return animate;
    }

    public void deal() {
        Set<String> set = new HashSet<>();
        List<String> strings = animateMapper.queryAllTag();
        strings.forEach(e -> {
            if (e == null) return;
            try {
                List<String> list = objectMapper.readValue(e, List.class);
                set.addAll(list);
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }
        });
        animateMapper.insertTags(set);
    }

}
