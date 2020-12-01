package dev.cheerfun.pixivic.biz.web.collection.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import dev.cheerfun.pixivic.biz.web.illust.domain.elasticsearch.ElasticsearchResponse;
import dev.cheerfun.pixivic.biz.web.illust.domain.elasticsearch.Hits;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * @date 2020/12/1 8:59 PM
 * @description CollectionSearchUtil
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CollectionSearchUtil {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    @Value("${elasticsearch.ip}")
    private String elasticsearch;
    private final static String PRE = "{";
    private final static String DOT = ",";
    private final static String POS = "}";
    private final static String KEYWORDS_PRE = "\"query\":{\"bool\":{\"should\":[";
    private final static String KEYWORD_PRE = "{\"match\":{\"tag_list\":\"";
    private final static String KEYWORD_END = "\"}}";
    private final static String KEYWORDS_END = "]";
    private final static String CREATE_DATE_PRE = "{\"range\":{\"create_time\":{";
    private final static String CREATE_DATE_END = "}}}";
    private final static String UPDATE_DATE_PRE = "{\"range\":{\"update_time\":{";
    private final static String UPDATE_DATE_END = "}}}";
    private final static String GTE = "\"gte\":\"";
    private final static String LTE = "\"lte\":\"";

    private final static String FILTER_PRE = "\"filter\":[{\"term\":{\"use_flag\":1}},{\"term\":{\"is_public\":1}}";
    private final static String FILTER_POS = "]}}}";

    private final static String PRE_SEARCH_BODY = "{\"min_score\":0.3,\"sort\":[\"_score\",{\"total_bookmarked\":{\"order\":\"desc\"}},{\"total_liked\":{\"order\":\"desc\"}},{\"create_time\":{\"order\":\"desc\"}}],";
    private final static String FROM = "\"from\":";
    private final static String SIZE = "\"size\": ";

    public String build(String keyword, String startCreateDate, String endCreateDate, String startUpdateDate, String endUpdateDate, Integer page, Integer pageSize) {
        StringBuilder stringBuilder = new StringBuilder(PRE_SEARCH_BODY);
        stringBuilder.append(FROM)
                .append((page - 1) * pageSize)
                .append(DOT)
                .append(SIZE)
                .append(pageSize)
                .append(DOT)
                .append(KEYWORDS_PRE);

        for (String s : keyword.split(" ")) {
            stringBuilder.append(KEYWORD_PRE)
                    .append(s)
                    .append(KEYWORD_END)
                    .append(DOT);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(KEYWORDS_END)
                .append(DOT)
                .append(FILTER_PRE);
        if (startCreateDate != null || endCreateDate != null) {
            stringBuilder.append(DOT)
                    .append(CREATE_DATE_PRE);
            if (startCreateDate != null) {
                stringBuilder.append(GTE)
                        .append(startCreateDate)
                        .append("\"")
                        .append(DOT);
            }
            if (endCreateDate != null) {
                stringBuilder.append(LTE)
                        .append(endCreateDate)
                        .append("\"");
            } else {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            stringBuilder.append(CREATE_DATE_END);
        }

        if (startUpdateDate != null || endUpdateDate != null) {
            stringBuilder.append(DOT)
                    .append(UPDATE_DATE_PRE);
            if (startUpdateDate != null) {
                stringBuilder.append(GTE)
                        .append(startUpdateDate)
                        .append("\"")
                        .append(DOT);
            }
            if (endUpdateDate != null) {
                stringBuilder.append(LTE)
                        .append(endUpdateDate)
                        .append("\"");
            } else {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            stringBuilder.append(CREATE_DATE_END);
        }

        stringBuilder.append(FILTER_POS);
        return stringBuilder.toString();
    }

    public CompletableFuture<List<Integer>> searchCollection(String body) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://" + elasticsearch + "/collections/_search?_source=collection_id"))
                .method("GET", HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(
                r -> {
                    ElasticsearchResponse<Collection> elasticsearchResponse;
                    try {
                        if (r.body() != null) {
                            elasticsearchResponse = objectMapper.readValue(r.body(), new TypeReference<>() {
                            });
                            Hits<Collection> hits = elasticsearchResponse.getHits();
                            if (hits != null && hits.getHits() != null) {
                                return hits.getHits().stream().map(h -> h.getT().getId()).collect(Collectors.toList());
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
