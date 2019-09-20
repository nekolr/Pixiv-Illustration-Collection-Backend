package dev.cheerfun.pixivic.web.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.util.JsonBodyHandler;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import dev.cheerfun.pixivic.web.common.util.YouDaoTranslatedUtil;
import dev.cheerfun.pixivic.web.search.exception.SearchException;
import dev.cheerfun.pixivic.web.search.model.Response.*;
import dev.cheerfun.pixivic.web.search.model.SearchSuggestion;
import dev.cheerfun.pixivic.web.search.util.ImageSearchUtil;
import dev.cheerfun.pixivic.web.search.util.SearchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private final ObjectMapper objectMapper;
    private final SearchUtil searchUtil;
    private final ImageSearchUtil imageSearchUtil;
    private Pattern moeGirlPattern = Pattern.compile("(?<=(?:title=\")).+?(?=\" data-serp-pos)");

    public PixivSearchCandidatesResponse getCandidateWords(String keyword) throws IOException, InterruptedException {
        return (PixivSearchCandidatesResponse) requestUtil.getJsonSync("https://proxy.pixivic.com:23334/v1/search/autocomplete?word=" + keyword, PixivSearchCandidatesResponse.class);
    }

    public List<SearchSuggestion> getSearchSuggestion(String keyword) throws IOException, InterruptedException {
        BangumiSearchResponse bangumi = getSearchSuggestionFromBangumi(keyword);
        List<SearchSuggestion> searchSuggestions = BangumiSearchResponse.castToSearchSuggestionList(bangumi);
        //平均分小于4则进行萌娘百科检索+有道翻译
        if (BangumiSearchResponse.getAvgSum(bangumi) < 4) {
            searchSuggestions.addAll(getSearchSuggestionFromMoeGirl(keyword));
        }
        return searchSuggestions;
    }

    public List<SearchSuggestion> getPixivSearchSuggestion(String keyword) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("accept-language", "zh-CN,zh;q=0.9")
                .uri(URI.create("https://proxy.pixivic.com:23334/search.php?s_mode=s_tag&word=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)))
                .GET()
                .build();
        String body = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        List<PixivSearchSuggestion> pixivSearchSuggestions = null;
        if (body.contains("data-related-tags")) {
            pixivSearchSuggestions = objectMapper.readValue(HtmlUtils.htmlUnescape(body.substring(body.indexOf("data-related-tags") + 19, body.indexOf("\"data-tag"))), new TypeReference<List<PixivSearchSuggestion>>() {
            });
        }
        return pixivSearchSuggestions != null ? pixivSearchSuggestions.stream().map(pixivSearchSuggestion -> new SearchSuggestion(pixivSearchSuggestion.getTag(), pixivSearchSuggestion.getTag_translation())).collect(Collectors.toList()) : null;
    }

    public SearchSuggestion getKeywordTranslation(String keyword) {
        return new SearchSuggestion(translatedByYouDao(keyword), keyword);
    }

    private BangumiSearchResponse getSearchSuggestionFromBangumi(String keyword) throws IOException, InterruptedException {
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
                .uri(URI.create("https://zh.moegirl.org/index.php?limit=2&search=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)))
                .build();
        //正则提取关键词
        String body = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        return moeGirlPattern.matcher(body).results().map(result -> {
            String matchKeyword = result.group();
            return new SearchSuggestion(matchKeyword, translatedByYouDao(keyword));
        }).collect(Collectors.toList());
    }

    public String translatedByYouDao(String keyword) {
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
            e.printStackTrace();
        }
        assert youdaoTranslatedResponse != null;
        List<String> keywordTranslated = youdaoTranslatedResponse.getResult();
        if (keywordTranslated == null) {
            throw new SearchException(HttpStatus.BAD_REQUEST, "关键词非中文，自动翻译失败");
        }
        return keywordTranslated.get(0);
    }

    public CompletableFuture<List<Illustration>> searchByKeyword(
            String keyword,
            int pageSize,
            int page,
            String searchType,
            String illustType,
            int minWidth,
            int minHeight,
            String beginDate,
            String endDate,
            int xRestrict,
            int popWeight,
            int minTotalBookmarks,
            int minTotalView) {
        return searchUtil.request(searchUtil.build(keyword, pageSize, page, searchType, illustType, minWidth, minHeight, beginDate, endDate, xRestrict, popWeight, minTotalBookmarks, minTotalView));
    }

    public CompletableFuture<SaucenaoResponse> searchByImage(String imageUrl) {
        return imageSearchUtil.searchBySaucenao(imageUrl);
    }

    public String getKeyword(HttpServletRequest request) {
        final String path =
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern =
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        String arguments = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path);
        String moduleName;
        if (!arguments.isEmpty() && arguments.contains("/")) {
            moduleName = arguments.substring(0, arguments.lastIndexOf("/"));
        } else {
            moduleName = "";
        }
        return URLDecoder.decode(moduleName, StandardCharsets.UTF_8);
    }
}
