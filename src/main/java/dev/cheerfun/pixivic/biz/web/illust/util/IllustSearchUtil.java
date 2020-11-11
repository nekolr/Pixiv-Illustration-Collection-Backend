package dev.cheerfun.pixivic.biz.web.illust.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.illust.domain.elasticsearch.ElasticsearchResponse;
import dev.cheerfun.pixivic.biz.web.illust.domain.elasticsearch.Hit;
import dev.cheerfun.pixivic.biz.web.illust.domain.elasticsearch.Hits;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/14 22:40
 * @description SearchUtil
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IllustSearchUtil {
    private final static String MIN_SCORE = "\"min_score\": 0.6";
    private final static String FROM = "\"from\":";
    private final static String SIZE = "\"size\": ";
    private final static String PRE = "{";
    private final static String DOT = ",";
    private final static String POS = "}";
    private final static String QUERY_PRE = /*"query":{"function_score":{*/"\"query\":{\"bool\":{";
    private final static String QUERY_SHOULD = /*"query":{"function_score":{*/"\"should\":[";
    private final static String FILTER_PRE = "],\"filter\":[";
    private final static String FILTER_POS = "]";
    private final static String QUERY_POS = "}}";
    private final static String NESTED_PRE = "{\"nested\":{\"path\":\"tags\",\"query\":{\"boosting\":{\"positive\":{\"match\":{\"tags.name\":{\"query\":\"";
    private final static String NESTED_POS = "\"}}},\"negative\":{\"match\":{\"tags.translated_name.keyword\":\"\"}},\"negative_boost\":0.865}}}}";
    private final static String TYPE_PRE = "{\"term\":{\"type\":\"";
    private final static String TYPE_POS = "\"}}";
    private final static String X_RESTRICT_PRE = "{\"term\":{\"restrict\":";
    private final static String X_RESTRICT_POS = "}}";
    private final static String ID_PRE = "\"must_not\": {\"term\": {\"_id\":\"";
    private final static String ID_POS = "\"}}";
    private final static String MIN_WIDTH_PRE = "{\"range\":{\"width\":{\"gte\":";
    private final static String MIN_WIDTH_POS = "}}}";
    private final static String MIN_BOOKMARK_PRE = "{\"range\":{\"total_bookmarks\":{\"gte\":";
    private final static String MIN_BOOKMARK_POS = "}}}";
    private final static String MIN_HEIGHT_PRE = "{\"range\":{\"height\":{\"gte\":";
    private final static String MIN_HEIGHT_POS = "}}}";
    private final static String MAX_SANITY_LEVEL_PRE = "{\"range\":{\"sanity_level\":{\"lte\":";
    private final static String MAX_SANITY_LEVEL_POS = "}}}";
    private final static String DATE_RANGE_1 = "{\"range\":{\"create_date\":{\"gte\":\"";
    private final static String DATE_RANGE_2 = "\",\"lte\":\"";
    private final static String DATE_RANGE_3 = "\"}}}";
    //private final static String SORT = "\"sort\":[\"_score\",{\"total_bookmarks\":{\"order\":\"desc\"}},{\"total_view\":{\"order\":\"desc\"}}],";
    private final static String SORT = "\"sort\":[\"_score\",{\"total_bookmarks\":{\"order\":\"desc\"}}],";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    @Lazy
    private final IllustrationBizService illustrationBizService;
    @Value("${elasticsearch.ip}")
    private String elasticsearch;

    public String build(
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
            Integer exceptId
    ) {
        StringBuilder stringBuilder = new StringBuilder(PRE);
        stringBuilder
                .append(MIN_SCORE)
                .append(DOT)
                .append(SORT)
                .append(FROM)
                .append((page - 1) * pageSize)
                .append(DOT)
                .append(SIZE)
                .append(pageSize)
                .append(DOT)
                .append(QUERY_PRE);
        //id
        if (exceptId != null) {
            stringBuilder.append(ID_PRE)
                    .append(exceptId)
                    .append(ID_POS)
                    .append(DOT);
        }
        stringBuilder.append(QUERY_SHOULD);
        String[] keywords = keyword.split("\\|\\|");
        for (String k : keywords) {
            stringBuilder.append(NESTED_PRE).append(HtmlUtils.htmlEscape(k)).append(NESTED_POS).append(DOT);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        //过滤器
        stringBuilder.append(FILTER_PRE);
        //画作类型
        if (illustType != null) {
            stringBuilder.append(TYPE_PRE)
                    .append(illustType)
                    .append(TYPE_POS)
                    .append(DOT);
        }
        //r18
        if (xRestrict != null) {
            stringBuilder.append(X_RESTRICT_PRE)
                    .append(xRestrict)
                    .append(X_RESTRICT_POS)
                    .append(DOT);
        }
        //开始日期结束日期
        if (beginDate != null && endDate != null) {
            stringBuilder.append(DATE_RANGE_1)
                    .append(beginDate)
                    .append(DATE_RANGE_2)
                    .append(endDate)
                    .append(DATE_RANGE_3)
                    .append(DOT);
        }
        //最小收藏数
        if (minTotalBookmarks != null) {
            stringBuilder.append(MIN_BOOKMARK_PRE)
                    .append(minTotalBookmarks)
                    .append(MIN_BOOKMARK_POS)
                    .append(DOT);
        }
        //最小高度
        if (minHeight != null) {
            stringBuilder.append(MIN_HEIGHT_PRE)
                    .append(minHeight)
                    .append(MIN_HEIGHT_POS)
                    .append(DOT);
        }
        //最小宽度
        if (minWidth != null) {
            stringBuilder.append(MIN_WIDTH_PRE)
                    .append(minWidth)
                    .append(MIN_WIDTH_POS)
                    .append(DOT);
        }
        stringBuilder.append(MAX_SANITY_LEVEL_PRE)
                .append(maxSanityLevel)
                .append(MAX_SANITY_LEVEL_POS)
                .append(DOT);
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(FILTER_POS);//.append(DOT);

        //末尾
        stringBuilder//.append(SCRIPT_SCORE)
                .append(QUERY_POS)
                .append(POS);
        return stringBuilder.toString();
    }

    public CompletableFuture<List<Illustration>> request(String body) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://" + elasticsearch + ":9200/illusts/_search?_source=illust_id"))
                .method("GET", HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(
                r -> {
                    ElasticsearchResponse<Illustration> elasticsearchResponse;
                    try {
                        if (r.body() != null) {
                            elasticsearchResponse = objectMapper.readValue(r.body(), new TypeReference<>() {
                            });
                            Hits<Illustration> hits = elasticsearchResponse.getHits();
                            if (hits != null && hits.getHits() != null) {
                                List<Integer> illustIdList = hits.getHits().stream().map(h -> h.getT().getId()).collect(Collectors.toList());

                                return illustrationBizService.queryIllustrationByIllustIdList(illustIdList);
                                //return new SearchResult(hits.getTotal().getValue(), illustrationList);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        );
    }

}
