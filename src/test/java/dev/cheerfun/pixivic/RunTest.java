package dev.cheerfun.pixivic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.cheerfun.pixivic.biz.web.illust.domain.SearchSuggestion;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RunTest {

    int i = 0;
    volatile int j = 0;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        String content = Files.readString(Path.of("/Users/oysterqaq/Desktop/1.txt"));
        Pattern moeGirlPattern = Pattern.compile("(?<=(?:gtm-illust-recommend-user-name\" href=\"/users\\/)).+?(?=\\\")");
        moeGirlPattern.matcher(content).results().forEach(result -> {
            String matchKeyword = result.group();
            System.out.println(matchKeyword + ",");
        });
    }

    public static boolean queryRelatedKeywordFromBaidu(String keyword) {
        try {
            //获取
            List<String> relatedKeyWordList = null;
            Document document = Jsoup.connect("https://www.baidu.com/s?wd=" + URLEncoder.encode(keyword)).get();
            Element rs = document.body().getElementById("rs");
            if (rs != null) {
                Elements a = rs.getElementsByTag("a");
                if (a != null && a.size() > 0) {
                    relatedKeyWordList = a.stream().map(Element::text).collect(Collectors.toList());
                    //更新关键词表
                    String keywordId = "select id from osc_keyword where content=? ";
                    System.out.println(relatedKeyWordList);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}

class Host {
    public String hostname;
    public Integer port;
    public String username;
    public String password;
    public String fingerprint;
}
