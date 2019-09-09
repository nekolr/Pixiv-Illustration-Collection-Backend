package dev.cheerfun.pixivic.web.search.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.web.search.model.SearchRequest;
import dev.cheerfun.pixivic.web.search.model.elasticsearch.ElasticsearchResponse;
import dev.cheerfun.pixivic.web.search.model.elasticsearch.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class SearchUtil {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;


    private final static String FROM = "\"from\":";
    private final static String SIZE = "\"size\": ";

    private final static String PRE = "{";
    private final static String DOT = ",";
    private final static String POS = "}";
    private final static String QUERY_PRE = "\"query\":{\"function_score\":{\"query\":{\"bool\":{\"should\":[";
    private final static String FILTER_PRE = "],\"filter\":[";
    private final static String FILTER_POS = "]}},";
    private final static String QUERY_POS = "}}";

    private final static String NESTED_PRE = "{\"nested\":{\"path\":\"tags\",\"query\":{\"boosting\":{\"positive\":{\"match\":{\"tags.name\":{\"query\":\"";
    private final static String NESTED_POS = "\"}}},\"negative\":{\"term\":{\"tags.translated_name.keyword\":\"\"}},\"negative_boost\":0.865}}}}";

    private final static String TYPE_PRE = "{\"term\":{\"type\":\"";
    private final static String TYPE_POS = "\"}}";

    private final static String X_RESTRICT_PRE = "{\"term\":{\"x_restrict\":";
    private final static String X_RESTRICT_POS = "}}";

    private final static String MIN_WIDTH_PRE = "{\"range\":{\"width\":{\"gte\":";
    private final static String MIN_WIDTH_POS = "}}}";

    private final static String MIN_HEIGHT_PRE = "{\"range\":{\"height\":{\"gte\":";
    private final static String MIN_HEIGHT_POS = "}}}";

    private final static String DATE_RANGE_1 = "{\"range\":{\"create_date\":{\"gte\":\"";
    private final static String DATE_RANGE_2 = "\",\"lte\":\"";
    private final static String DATE_RANGE_3 = "\"}}}";

    private final static String SCRIPT_SCORE = "\"script_score\":{\"script\":{\"params\":{\"total_bookmarks_max\":2545340,\"total_view_max\":27640400},\"source\":\"((doc['total_bookmarks'].value/params.total_bookmarks_max+doc['total_view'].value/params.total_view_max)+1)\"}}";

    public String build(SearchRequest searchRequest) {
        StringBuilder stringBuilder = new StringBuilder(PRE);
        stringBuilder.append(FROM)
                .append((searchRequest.getPage() - 1) * searchRequest.getPageSize())
                .append(DOT)
                .append(SIZE)
                .append(searchRequest.getPageSize())
                .append(DOT)
                .append(QUERY_PRE);
        String[] keywords = searchRequest.getKeyword().split(" ");

        for (String keyword : keywords) {
            stringBuilder.append(NESTED_PRE).append(keyword).append(NESTED_POS).append(DOT);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        //过滤器
        stringBuilder.append(FILTER_PRE)
                .append(TYPE_PRE)
                .append(searchRequest.getIllustType())
                .append(TYPE_POS)
                .append(DOT)
                .append(X_RESTRICT_PRE)
                .append(searchRequest.getXRestrict())
                .append(X_RESTRICT_POS)
                .append(DOT);
        if (searchRequest.getBeginDate() != null && searchRequest.getEndDate() != null) {
            stringBuilder.append(DATE_RANGE_1)
                    .append(searchRequest.getBeginDate())
                    .append(DATE_RANGE_2)
                    .append(searchRequest.getEndDate())
                    .append(DATE_RANGE_3)
                    .append(DOT);
        }
        if (searchRequest.getMinWidth() != 0) {
            stringBuilder.append(MIN_WIDTH_PRE)
                    .append(searchRequest.getMinWidth())
                    .append(MIN_WIDTH_POS)
                    .append(DOT);
        }
        if (searchRequest.getMinHeight() != 0) {
            stringBuilder.append(MIN_HEIGHT_PRE)
                    .append(searchRequest.getMinHeight())
                    .append(MIN_HEIGHT_POS)
                    .append(DOT);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(FILTER_POS);

        //末尾
        stringBuilder.append(SCRIPT_SCORE)
                .append(QUERY_POS)
                .append(POS);

        return stringBuilder.toString();
    }

    public CompletableFuture<List<Illustration>> request(String body) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Content-Type","application/json")
                .uri(URI.create("http://es:9200/illust/_search"))
                .method("GET", HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenApply(
                b -> {
                    System.out.println(b);
                    ElasticsearchResponse elasticsearchResponse = null;
                    try {
                        elasticsearchResponse = objectMapper.readValue(b, new TypeReference<ElasticsearchResponse>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert elasticsearchResponse != null;
                    return elasticsearchResponse.getHits().getHits().stream().map(Hit::getIllustration).collect(Collectors.toList());
                }
        );
    }

}
