package dev.cheerfun.pixivic.biz.web.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.mapper.IllustrationMapper;
import dev.cheerfun.pixivic.biz.web.common.util.YouDaoTranslatedUtil;
import dev.cheerfun.pixivic.biz.web.search.dto.PixivSearchSuggestionDTO;
import dev.cheerfun.pixivic.biz.web.search.dto.SearchSuggestionSyncDTO;
import dev.cheerfun.pixivic.biz.web.search.dto.TagTranslation;
import dev.cheerfun.pixivic.biz.web.search.exception.SearchException;
import dev.cheerfun.pixivic.biz.web.search.mapper.PixivSuggestionMapper;
import dev.cheerfun.pixivic.biz.web.search.model.Response.BangumiSearchResponse;
import dev.cheerfun.pixivic.biz.web.search.model.Response.PixivSearchCandidatesResponse;
import dev.cheerfun.pixivic.biz.web.search.model.Response.SaucenaoResponse;
import dev.cheerfun.pixivic.biz.web.search.model.Response.YoudaoTranslatedResponse;
import dev.cheerfun.pixivic.biz.web.search.model.SearchSuggestion;
import dev.cheerfun.pixivic.biz.web.search.util.ImageSearchUtil;
import dev.cheerfun.pixivic.biz.web.search.util.SearchUtil;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import dev.cheerfun.pixivic.common.util.JsonBodyHandler;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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
    private final PixivSuggestionMapper pixivSuggestionMapper;
    private final IllustrationMapper illustrationMapper;
    private static volatile ConcurrentHashMap<String, List<SearchSuggestion>> waitSaveToDb = new ConcurrentHashMap(5000);
    private Pattern moeGirlPattern = Pattern.compile("(?<=(?:title=\")).+?(?=\" data-serp-pos)");

    public CompletableFuture<PixivSearchCandidatesResponse> getCandidateWords(String keyword) {
        return requestUtil.getJson("https://proxy.pixivic.com:23334/v1/search/autocomplete?word=" + URLEncoder.encode(keyword, Charset.defaultCharset()))
                .thenApply(r -> {
                    PixivSearchCandidatesResponse pixivSearchCandidatesResponse = null;
                    try {
                        pixivSearchCandidatesResponse = objectMapper.readValue(r, new TypeReference<PixivSearchCandidatesResponse>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return pixivSearchCandidatesResponse;
                });
    }

    public CompletableFuture<List<SearchSuggestion>> getSearchSuggestion(String keyword) {
        return getSearchSuggestionFromBangumi(keyword).thenCompose(result -> {
            List<SearchSuggestion> searchSuggestions = BangumiSearchResponse.castToSearchSuggestionList(result);
            //平均分小于4则进行萌娘百科检索+有道翻译
            if (BangumiSearchResponse.getAvgSum(result) < 4) {
                return getSearchSuggestionFromMoeGirl(keyword).whenComplete((r, t) -> {
                    searchSuggestions.addAll(r);
                });
            }
            return CompletableFuture.completedFuture(searchSuggestions);
        });

    }

    public CompletableFuture<List<SearchSuggestion>> getPixivSearchSuggestion(String keyword) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("accept-language", "zh-CN,zh;q=0.9")
                .uri(URI.create("https://proxy.pixivic.com:23334/ajax/search/artworks/" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)))
                .GET()
                .build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(r -> {
            String body = r.body();
            try {
                PixivSearchSuggestionDTO pixivSearchSuggestionDTO = objectMapper.readValue(body, PixivSearchSuggestionDTO.class);
                Map<String, TagTranslation> tagTranslation = pixivSearchSuggestionDTO.getBody().getTagTranslation();
                List<String> relatedTags = pixivSearchSuggestionDTO.getBody().getRelatedTags();
                List<SearchSuggestion> searchSuggestions = relatedTags.stream().map(e -> new SearchSuggestion(e, tagTranslation.get(e)==null? "":tagTranslation.get(e).getZh())).collect(Collectors.toList());
                if (searchSuggestions.size() > 0) {
                    //保存
                    waitSaveToDb.put(keyword, searchSuggestions);
                }
                return searchSuggestions;
            } catch (IOException e) {
                e.printStackTrace();
            }
            // List<SearchSuggestion> searchSuggestions = null;
            return null;
        });
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    private void savePixivSuggestionToDb() {
        final HashMap<String, List<SearchSuggestion>> temp = new HashMap<>(waitSaveToDb);
        waitSaveToDb.clear();
        //持久化
        if (!temp.isEmpty()) {
            temp.keySet().forEach(e -> {
                List<Tag> searchSuggestions = temp.get(e).stream().map(t -> new Tag(t.getKeyword(), t.getKeywordTranslated())).collect(Collectors.toList());
                pixivSuggestionMapper.insert(e, searchSuggestions);

            });
            //取出没有id的suggest
            List<Tag> tags = pixivSuggestionMapper.queryByNoSuggestId().stream().map(SearchSuggestionSyncDTO::getSearchSuggestion).collect(Collectors.toList());
            if (tags.size() > 0) {
                illustrationMapper.insertTag(tags);
                //获取标签id并写回
                tags.forEach(tag -> {
                    Long tagId = illustrationMapper.getTagId(tag.getName(), tag.getTranslatedName());
                    pixivSuggestionMapper.updateSuggestionTagId(tag, tagId);
                });
            }
        }

    }

    public SearchSuggestion getKeywordTranslation(String keyword) {
        return new SearchSuggestion(translatedByYouDao(keyword), keyword);
    }

    private CompletableFuture<BangumiSearchResponse> getSearchSuggestionFromBangumi(String keyword) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.bgm.tv/search/subject/" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) + "?app_id=bgm11725d4d9360d4cf5&max_results=3&responseGroup=large&start=0"))
                .build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(r -> {
            BangumiSearchResponse bangumiSearchResponse = null;
            try {
                bangumiSearchResponse = objectMapper.readValue(r.body(), new TypeReference<BangumiSearchResponse>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bangumiSearchResponse;
        });
    }

    public CompletableFuture<List<SearchSuggestion>> getSearchSuggestionFromMoeGirl(String keyword) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://zh.moegirl.org/index.php?limit=2&search=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)))
                .build();
        //正则提取关键词
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(r ->
                moeGirlPattern.matcher(r.body()).results().map(result -> {
                    String matchKeyword = result.group();
                    return new SearchSuggestion(matchKeyword, translatedByYouDao(keyword));
                }).collect(Collectors.toList())
        );

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
            int minTotalView,
            int maxSanityLevel) {
        return searchUtil.request(searchUtil.build(keyword, pageSize, page, searchType, illustType, minWidth, minHeight, beginDate, endDate, xRestrict, popWeight, minTotalBookmarks, minTotalView, maxSanityLevel));
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
