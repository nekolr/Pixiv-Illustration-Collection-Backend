package dev.cheerfun.pixivic;

import dev.cheerfun.pixivic.biz.crawler.bangumi.domain.Animate;
import dev.cheerfun.pixivic.biz.crawler.bangumi.domain.AnimateCharacter;
import dev.cheerfun.pixivic.biz.crawler.bangumi.domain.Seiyuu;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class RunTest {
    public static void main(String[] args) throws IOException, TimeoutException {

        final String body = "";
        Animate animate = new Animate();
        Document doc = Jsoup.parse(body);
        Element nameSingle = doc.getElementsByClass("nameSingle").get(0);
        Element title = nameSingle.child(0);
        animate.setType(nameSingle.child(1).text());
        animate.setId(276150);
        animate.setTitle(title.text());
        animate.setTranslatedTitle(title.attr("title"));
        animate.setIntro(doc.getElementById("subject_summary").text());
        Elements tags = doc.getElementsByClass("subject_tag_section").get(0).getElementsByClass("inner").get(0).getElementsByTag("a");
        animate.setTags(tags.stream().map(t -> t.child(0).text()).collect(Collectors.toList()));
        animate.setRate(Float.valueOf(doc.getElementsByClass("global_score").get(0).child(0).text()));
        Elements infobox = doc.getElementById("infobox").getElementsByTag("li");
        String cover = "https:" + doc.getElementsByClass("thickbox cover").get(0).attr("href");
        animate.setCover(cover);
        HashMap<String, String> detail = new HashMap<>();
        infobox.stream()
                .forEach(
                        e -> {
                            String key = e.getElementsByClass("tip").text().replace(":", "");
                            String text = e.text();
                            String value = text.substring(text.indexOf(": ") + 2);
                            detail.merge(key, value, (a, b) -> a + "|" + b);
                        });
        animate.setDetail(detail);
        System.out.println(animate);
        String charactersBody = "";
        Document charactersDoc = Jsoup.parse(charactersBody);
        Element columnInSubjectA = charactersDoc.getElementById("columnInSubjectA");
        Elements light_odd = columnInSubjectA.getElementsByClass("light_odd");
        light_odd.stream().map(e -> {
            AnimateCharacter animateCharacter = new AnimateCharacter();
            animateCharacter.setId(Integer.valueOf(Optional.of(e.child(0).attr("href").replace("/character/", "")).orElse("null")));
            animateCharacter.setAvatar("https:" + e.child(0).child(0).attr("src"));
            Element clearit = e.getElementsByClass("clearit").get(0);
            Element h2 = clearit.child(0);
            animateCharacter.setName(h2.child(0).text());
            animateCharacter.setTranslatedName(h2.child(1).text().replace("/ ", ""));
            Element crt_info = clearit.child(1);
            animateCharacter.setType(crt_info.child(0).text());
            animateCharacter.setDetail(crt_info.child(1).text());

            Element actorBadge_clearit = clearit.child(2);
            if (actorBadge_clearit != null) {
                Seiyuu seiyuu = new Seiyuu();
                seiyuu.setAvatar("https:" + actorBadge_clearit.getElementsByClass("avatar ll").attr("src"));
                seiyuu.setId(Integer.valueOf(actorBadge_clearit.child(0).attr("href").replace("/person/", "")));
                seiyuu.setName(actorBadge_clearit.getElementsByClass("l").get(0).text());
                seiyuu.setTranslatedName(actorBadge_clearit.getElementsByClass("grey").get(0).text());
                animateCharacter.setSeiyuu(seiyuu);
            }
            System.out.println(animateCharacter);
            return animateCharacter;
        }).collect(Collectors.toList());
    }
}
