package dev.cheerfun.pixivic.web.search.service;

import dev.cheerfun.pixivic.common.util.JsonBodyHandler;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import dev.cheerfun.pixivic.web.search.model.Response.BangumiSearchResponse;
import dev.cheerfun.pixivic.web.search.model.Response.PixivSearchCandidatesResponse;
import dev.cheerfun.pixivic.web.search.model.Response.YoudaoTranslatedResponse;
import dev.cheerfun.pixivic.web.search.model.SearchSuggestion;
import dev.cheerfun.pixivic.web.search.util.YouDaoTranslatedUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
 * @date 2019/08/14 22:49
 * @description SearchService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SearchService {
    private final RequestUtil requestUtil;
    private final HttpClient httpClient;

    public PixivSearchCandidatesResponse getCandidateWords(String keyword) throws IOException, InterruptedException {
        return (PixivSearchCandidatesResponse) requestUtil.getJsonSync("https://proxy.pixivic.com:23334/v1/search/autocomplete?word=" + keyword, PixivSearchCandidatesResponse.class);
    }

    public List<SearchSuggestion> getSearchSuggestion(String keyword) throws IOException, InterruptedException {
        BangumiSearchResponse bangumi = getSearchSuggestionFromBangumi(keyword);
        List<SearchSuggestion> searchSuggestions = BangumiSearchResponse.castToSearchSuggestionList(bangumi);
        //平均分小于4则进行萌娘百科检索+有道翻译
        if (BangumiSearchResponse.getAvgSum(bangumi) < 4D) {
            System.out.println();
        }
        return searchSuggestions;
    }

    public BangumiSearchResponse getSearchSuggestionFromBangumi(String keyword) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.bgm.tv/search/subject/" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) + "?app_id=bgm11725d4d9360d4cf5&max_results=3&responseGroup=large&start=0"))
                .build();
        BangumiSearchResponse bangumiSearchResponse = (BangumiSearchResponse) httpClient.send(httpRequest, JsonBodyHandler.jsonBodyHandler(BangumiSearchResponse.class)).body();
        return bangumiSearchResponse;
    }

    public List<SearchSuggestion> getSearchSuggestionFromMoeGirl(String keyword) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://zh.moegirl.org/index.php?search=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)))
                .build();
        //正则提取关键词

        BangumiSearchResponse bangumiSearchResponse = (BangumiSearchResponse) httpClient.send(httpRequest, JsonBodyHandler.jsonBodyHandler(BangumiSearchResponse.class)).body();
        return BangumiSearchResponse.castToSearchSuggestionList(bangumiSearchResponse);
    }

    public String translatedByYouDao(String keyword) throws IOException, InterruptedException {
        Map<String, String> params = new HashMap<>();
        System.out.println(new String(keyword.getBytes(), StandardCharsets.UTF_8));
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
        YoudaoTranslatedResponse youdaoTranslatedResponse = (YoudaoTranslatedResponse) httpClient.send(httpRequest, JsonBodyHandler.jsonBodyHandler(YoudaoTranslatedResponse.class)).body();
        return youdaoTranslatedResponse.getResult().get(0);
    }

}
