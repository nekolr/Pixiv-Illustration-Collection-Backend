package dev.cheerfun.pixivic.biz.web.artist.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.artist.dto.ArtistSearchDTO;
import dev.cheerfun.pixivic.biz.web.illust.domain.elasticsearch.ElasticsearchResponse;
import dev.cheerfun.pixivic.biz.web.illust.domain.elasticsearch.Hit;
import dev.cheerfun.pixivic.biz.web.illust.domain.elasticsearch.Hits;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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
 * @date 2020/4/19 12:18 下午
 * @description SearchUtil
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArtistSearchUtil {
    private final static String PRE = "{";
    private final static String FROM = "\"from\":";
    private final static String SIZE = "\"size\":";
    private final static String DOT = ",";
    private final static String QUERY = "\"query\":{\"match\":{\"name\":\"";
    private final static String POS = "\"}}}";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    @Value("${elasticsearch.ip}")
    private String elasticsearch;

    @Cacheable("artistSearchResult")
    public CompletableFuture<List<ArtistSearchDTO>> search(String artistName, Integer page, Integer pageSize) {
        String build = build(artistName, page, pageSize);
        return request(build);
    }

    public String build(String artistName, Integer page, Integer pageSize) {
        String stringBuilder = PRE + FROM +
                (page - 1) * pageSize +
                DOT +
                SIZE +
                pageSize +
                DOT +
                QUERY +
                HtmlUtils.htmlEscape(artistName) +
                POS;
        return stringBuilder;
    }

    public CompletableFuture<List<ArtistSearchDTO>> request(String body) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://" + elasticsearch + ":9200/artist/_search"))
                .method("GET", HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(
                r -> {
                    ElasticsearchResponse<ArtistSearchDTO> elasticsearchResponse;
                    try {
                        if (r.body() != null) {
                            elasticsearchResponse = objectMapper.readValue(r.body(), new TypeReference<>() {
                            });
                            Hits<ArtistSearchDTO> hits = elasticsearchResponse.getHits();
                            if (hits != null && hits.getHits() != null) {
                                return hits.getHits().stream().map(Hit::getT).collect(Collectors.toList());
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
