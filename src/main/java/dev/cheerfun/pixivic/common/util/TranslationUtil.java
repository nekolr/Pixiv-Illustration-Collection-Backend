package dev.cheerfun.pixivic.common.util;

import dev.cheerfun.pixivic.biz.web.common.util.YouDaoTranslatedUtil;
import dev.cheerfun.pixivic.biz.web.search.domain.response.YoudaoTranslatedResponse;
import dev.cheerfun.pixivic.common.util.json.JsonBodyHandler;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/3 11:04 上午
 * @description TranslationUtil
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TranslationUtil {

    public static final String appid = "20200810000539396";
    public static final String securityKey = "moKfaw4ozBRPNMGTtQzR";

    private final HttpClient httpClient;

    @Cacheable(value = "translateToJP")
    public String translateToJapaneseByYouDao(String keyword) {
        Map<String, String> params = new HashMap<>();
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("from", "auto");
        params.put("to", "ja");
        params.put("signType", "v3");
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        params.put("curtime", curtime);
        String signStr = "6f8d12eb52dab6e2" + YouDaoTranslatedUtil.truncate(keyword) + salt + curtime + "FCTSvDZueYlrnOYVTgdzHJFR3d9b5lZc";
        String sign = YouDaoTranslatedUtil.getDigest(signStr);
        params.put("appKey", "6f8d12eb52dab6e2");
        params.put("q", URLEncoder.encode(keyword, StandardCharsets.UTF_8));
        params.put("salt", salt);
        params.put("sign", sign);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://openapi.youdao.com/api?" + RequestUtil.getPostEntity(params)))
                .GET()
                .build();
        YoudaoTranslatedResponse youdaoTranslatedResponse = null;
        try {
            youdaoTranslatedResponse = (YoudaoTranslatedResponse) httpClient.send(httpRequest, JsonBodyHandler.jsonBodyHandler(YoudaoTranslatedResponse.class)).body();
        } catch (IOException | InterruptedException e) {
            //throw new SearchException(HttpStatus.BAD_REQUEST, e.getMessage());
            return "";
        }
        if (youdaoTranslatedResponse != null) {
            List<String> keywordTranslated = youdaoTranslatedResponse.getResult();
            if (keywordTranslated == null) {
                //throw new SearchException(HttpStatus.BAD_REQUEST, "关键词非中文，自动翻译失败");
                return "";
            }
            return keywordTranslated.get(0);
        }
        //throw new SearchException(HttpStatus.BAD_REQUEST, "自动翻译失败");
        return "";
    }

    @Cacheable(value = "translateToCN")
    public String translateToChineseByYouDao(String keyword) {
        Map<String, String> params = new HashMap<>();
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("from", "ja");
        params.put("to", "zh-CHS");
        params.put("signType", "v3");
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        params.put("curtime", curtime);
        String signStr = "6f8d12eb52dab6e2" + YouDaoTranslatedUtil.truncate(keyword) + salt + curtime + "FCTSvDZueYlrnOYVTgdzHJFR3d9b5lZc";
        String sign = YouDaoTranslatedUtil.getDigest(signStr);
        params.put("appKey", "6f8d12eb52dab6e2");
        params.put("q", URLEncoder.encode(keyword, StandardCharsets.UTF_8));
        params.put("salt", salt);
        params.put("sign", sign);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://openapi.youdao.com/api?" + RequestUtil.getPostEntity(params)))
                .GET()
                .build();
        YoudaoTranslatedResponse youdaoTranslatedResponse = null;
        try {
            youdaoTranslatedResponse = (YoudaoTranslatedResponse) httpClient.send(httpRequest, JsonBodyHandler.jsonBodyHandler(YoudaoTranslatedResponse.class)).body();
        } catch (IOException | InterruptedException e) {
            //throw new SearchException(HttpStatus.BAD_REQUEST, e.getMessage());
            return "";
        }
        if (youdaoTranslatedResponse != null) {
            List<String> keywordTranslated = youdaoTranslatedResponse.getResult();
            if (keywordTranslated == null) {
                //throw new SearchException(HttpStatus.BAD_REQUEST, "关键词非中文，自动翻译失败");
                return "";
            }
            return keywordTranslated.get(0);
        }
        //throw new SearchException(HttpStatus.BAD_REQUEST, "自动翻译失败");
        return "";
    }

    public String translateToChineseByBaidu(String keyword) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://api.fanyi.baidu.com/api/trans/vip/translate?" + RequestUtil.getPostEntity(buildParams(keyword, "auto", "zh"))))
                .GET()
                .build();
        try {
            String body = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println(body);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    //@PostConstruct
    public void test() {
        translateToChineseByBaidu("オリジナル美少女");
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", appid);
        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);
        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));
        return params;
    }
}
