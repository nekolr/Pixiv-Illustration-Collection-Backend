package dev.cheerfun.pixivic.common.util;

import dev.cheerfun.pixivic.biz.web.common.util.YouDaoTranslatedUtil;
import dev.cheerfun.pixivic.biz.web.search.domain.response.YoudaoTranslatedResponse;
import dev.cheerfun.pixivic.common.util.json.JsonBodyHandler;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
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
        String signStr = "6f8d12eb52dab6e2" + YouDaoTranslatedUtil.truncate(keyword) + salt + curtime + "B1VBsUhi3G7u4H17tncOwtGi93J2y1cX";
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
        String signStr = "6f8d12eb52dab6e2" + YouDaoTranslatedUtil.truncate(keyword) + salt + curtime + "B1VBsUhi3G7u4H17tncOwtGi93J2y1cX";
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
}
