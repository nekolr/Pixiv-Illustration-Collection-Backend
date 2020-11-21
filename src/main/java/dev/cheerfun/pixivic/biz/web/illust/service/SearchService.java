package dev.cheerfun.pixivic.biz.web.illust.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.biz.crawler.pixiv.secmapper.IllustrationMapper;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.illust.domain.SearchSuggestion;
import dev.cheerfun.pixivic.biz.web.illust.domain.response.BangumiSearchResponse;
import dev.cheerfun.pixivic.biz.web.illust.domain.response.PixivSearchCandidatesResponse;
import dev.cheerfun.pixivic.biz.web.illust.dto.PixivSearchSuggestionDTO;
import dev.cheerfun.pixivic.biz.web.illust.dto.SearchSuggestionSyncDTO;
import dev.cheerfun.pixivic.biz.web.illust.dto.TagTranslation;
import dev.cheerfun.pixivic.biz.web.illust.exception.SearchException;
import dev.cheerfun.pixivic.biz.web.illust.secmapper.PixivSuggestionMapper;
import dev.cheerfun.pixivic.biz.web.illust.util.IllustSearchUtil;
import dev.cheerfun.pixivic.biz.web.illust.util.ImageSearchUtil;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import dev.cheerfun.pixivic.common.util.translate.service.TranslationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.PostConstruct;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
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
    private static volatile ConcurrentHashMap<String, List<SearchSuggestion>> waitSaveToDb = new ConcurrentHashMap(10000);
    private final TranslationUtil translationUtil;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final IllustSearchUtil searchUtil;
    private final ImageSearchUtil imageSearchUtil;
    private final PixivSuggestionMapper pixivSuggestionMapper;
    private final IllustrationMapper illustrationMapper;
    private final IllustrationBizService illustrationBizService;
    private final ExecutorService saveToDBExecutorService;
    private Pattern moeGirlPattern = Pattern.compile("(?<=(?:title=\")).+?(?=\" data-serp-pos)");

    @PostConstruct
    public void init() {
        savePixivSuggestionToDb();
    }

    @Cacheable(value = "candidateWords")
    public CompletableFuture<PixivSearchCandidatesResponse> getCandidateWords(@SensitiveCheck String keyword) {
/*        return requestUtil.getJson("http://proxy.pixivic.com:23334/v1/search/autocomplete?word=" + URLEncoder.encode(keyword, Charset.defaultCharset()))
                .orTimeout(2, TimeUnit.SECONDS)
                .thenApply(r -> {
                    PixivSearchCandidatesResponse pixivSearchCandidatesResponse = null;
                    try {
                        pixivSearchCandidatesResponse = objectMapper.readValue(r, new TypeReference<PixivSearchCandidatesResponse>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return pixivSearchCandidatesResponse;
                });*/
        throw new BusinessException(HttpStatus.BAD_REQUEST, "暂时不可用");
    }

    @Cacheable(value = "searchSuggestions")
    public CompletableFuture<List<SearchSuggestion>> getSearchSuggestion(String keyword) {
      /*  return getSearchSuggestionFromBangumi(keyword)
                .orTimeout(2, TimeUnit.SECONDS)
                .thenCompose(result -> {
                    List<SearchSuggestion> searchSuggestions = BangumiSearchResponse.castToSearchSuggestionList(result);
                    //平均分小于4则进行萌娘百科检索+有道翻译
                    if (BangumiSearchResponse.getAvgSum(result) < 4) {
                        return getSearchSuggestionFromMoeGirl(keyword).whenComplete((r, t) -> {
                            searchSuggestions.addAll(r);
                        });
                    }
                    return CompletableFuture.completedFuture(searchSuggestions);
                });*/
        throw new BusinessException(HttpStatus.BAD_REQUEST, "暂时不可用");

    }

    @Cacheable(value = "pixivSearchSuggestions")
    public CompletableFuture<List<SearchSuggestion>> getPixivSearchSuggestion(@SensitiveCheck String keyword) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("accept-language", "zh-CN,zh;q=0.9")
                .uri(URI.create("http://proxy.pixivic.com:23334/ajax/search/artworks/" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)))
                .GET()
                .build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                // .orTimeout(2, TimeUnit.SECONDS)
                .thenApply(r -> {
                    String body = r.body();
                    if (!body.contains("\"body\":[]") && !body.contains("\"tagTranslation\":[]"))
                        try {
                            PixivSearchSuggestionDTO pixivSearchSuggestionDTO = objectMapper.readValue(body, PixivSearchSuggestionDTO.class);
                            Map<String, TagTranslation> tagTranslation = pixivSearchSuggestionDTO.getBody().getTagTranslation();
                            List<String> relatedTags = pixivSearchSuggestionDTO.getBody().getRelatedTags();
                            List<SearchSuggestion> searchSuggestions = relatedTags.stream().map(e -> new SearchSuggestion(e, tagTranslation.get(e) == null ? "" : tagTranslation.get(e).getZh())).collect(Collectors.toList());
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
        // throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "暂时不可用");
    }

    public void savePixivSuggestionToDb() {
        saveToDBExecutorService.submit(() -> {
            while (true) {
                try {
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
                    Thread.sleep(1000 * 60 * 5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public SearchSuggestion getKeywordTranslation(String keyword) {
        return new SearchSuggestion(translationUtil.translateToJapaneseByYouDao(keyword), keyword);
    }

    @Cacheable(value = "bangumiSearch")
    public CompletableFuture<BangumiSearchResponse> getSearchSuggestionFromBangumi(String keyword) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.bgm.tv/search/subject/" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) + "?app_id=bgm11725d4d9360d4cf5&max_results=3&responseGroup=large&start=0"))
                .build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(r -> {
            BangumiSearchResponse bangumiSearchResponse;
            try {
                bangumiSearchResponse = objectMapper.readValue(r.body(), new TypeReference<BangumiSearchResponse>() {
                });
            } catch (IOException e) {
                throw new SearchException(HttpStatus.NOT_FOUND, "未找到搜索建议");
            }
            return bangumiSearchResponse;
        });
    }

    @Cacheable(value = "searchSuggestions")
    public CompletableFuture<List<SearchSuggestion>> getSearchSuggestionFromMoeGirl(@SensitiveCheck String keyword) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://zh.moegirl.org/index.php?limit=2&search=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)))
                .build();
        //正则提取关键词
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(r ->
                moeGirlPattern.matcher(r.body()).results().map(result -> {
                    String matchKeyword = result.group();
                    return new SearchSuggestion(matchKeyword, translationUtil.translateToJapaneseByYouDao(keyword));
                }).collect(Collectors.toList())
        );

    }

    public Illustration queryFirstSearchResult(String keyword) throws ExecutionException, InterruptedException {
        List<Integer> illustIdList = searchByKeyword(keyword, 1, 0, "original", "illust", null, null, null, null, 0, null, null, null, 5, null).get();
        if (illustIdList != null && illustIdList.size() > 0) {
            return illustrationBizService.queryIllustrationByIdFromDb(illustIdList.get(0));
        }
        return null;
    }

    @Cacheable(value = "searchResult")
    public CompletableFuture<List<Integer>> searchByKeyword(
            String keyword,
            int pageSize,
            int page,
            String searchType,
            String illustType,
            Integer minWidth,
            Integer minHeight,
            String beginDate,
            String endDate,
            Integer xRestrict,
            Integer popWeight,
            Integer minTotalBookmarks,
            Integer minTotalView,
            Integer maxSanityLevel,
            Integer exceptId) {
        String build = searchUtil.build(keyword, pageSize, page, searchType, illustType, minWidth, minHeight, beginDate, endDate, xRestrict, popWeight, minTotalBookmarks, minTotalView, maxSanityLevel, exceptId);
        CompletableFuture<List<Integer>> request = searchUtil.request(build);
        return request;
    }

    @Cacheable(value = "saucenaoResponse")
    public CompletableFuture<List<Illustration>> searchByImage(String imageUrl) {
        return imageSearchUtil.searchBySaucenao(imageUrl).thenApply(r -> {
            if (r != null && r.getPixivIdList() != null) {
                return illustrationBizService.queryIllustrationByIdList(r.getPixivIdList().collect(Collectors.toList()));
            }
            throw new SearchException(HttpStatus.NOT_FOUND, "未找到画作");
        });
    }

    @Cacheable(value = "related")
    public CompletableFuture<List<Integer>> queryIllustrationRelated(int illustId, int page, int pageSize) {
        Illustration illustration = illustrationBizService.queryIllustrationById(illustId);
        if (illustration != null && illustration.getTags().size() > 0) {
            String keywords = illustration.getTags().stream().filter(e -> !"".equals(e.getName())).limit(3).map(Tag::getName).reduce((x, y) -> x + "||" + y).get();
            return searchByKeyword(keywords, pageSize, page, "original", null, null, null, null, null, 0, null, null, null, 5, illustId);
        }
        throw new BusinessException(HttpStatus.NOT_FOUND, "画作不存在");
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
