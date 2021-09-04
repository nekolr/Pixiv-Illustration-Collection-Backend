package dev.cheerfun.pixivic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.cheerfun.pixivic.biz.web.illust.domain.SearchSuggestion;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.commons.lang.StringUtils;
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

    public static int versionCompare(String v1, String v2) {
        int v1Len = StringUtils.countMatches(v1, ".");
        int v2Len = StringUtils.countMatches(v2, ".");

        if (v1Len != v2Len) {
            int count = Math.abs(v1Len - v2Len);
            if (v1Len > v2Len) {
                for (int i = 1; i <= count; i++) {
                    v2 += ".0";
                }
            } else {
                for (int i = 1; i <= count; i++) {
                    v1 += ".0";
                }
            }
        }

        if (v1.equals(v2)) {
            return 0;
        }

        String[] v1Str = StringUtils.split(v1, ".");
        String[] v2Str = StringUtils.split(v2, ".");
        for (int i = 0; i < v1Str.length; i++) {
            String str1 = "", str2 = "";
            for (char c : v1Str[i].toCharArray()) {
                if (Character.isLetter(c)) {
                    int u = c - 'a' + 1;
                    if (u < 10) {
                        str1 += String.valueOf("0" + u);
                    } else {
                        str1 += String.valueOf(u);
                    }
                } else {
                    str1 += String.valueOf(c);
                }
            }
            for (char c : v2Str[i].toCharArray()) {
                if (Character.isLetter(c)) {
                    int u = c - 'a' + 1;
                    if (u < 10) {
                        str2 += String.valueOf("0" + u);
                    } else {
                        str2 += String.valueOf(u);
                    }
                } else {
                    str2 += String.valueOf(c);
                }
            }
            v1Str[i] = "1" + str1;
            v2Str[i] = "1" + str2;

            int num1 = Integer.parseInt(v1Str[i]);
            int num2 = Integer.parseInt(v2Str[i]);

            if (num1 != num2) {
                if (num1 > num2) {
                    return 1;
                } else {
                    return 2;
                }
            }
        }
        return -1;
    }
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        System.out.println(versionCompare("4.0.0", "2.1.0") == 1);
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
