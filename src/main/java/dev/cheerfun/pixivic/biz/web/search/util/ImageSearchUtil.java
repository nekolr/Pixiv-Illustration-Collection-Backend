package dev.cheerfun.pixivic.biz.web.search.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.search.domain.response.SaucenaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/10 9:54
 * @description ImageSearchUtil
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImageSearchUtil {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final static String SAUCENAO_URL_1 = "https://saucenao.com/search.php?db=999&output_type=2&testmode=1&numres=3&url=";
    private final static String SAUCENAO_URL_2 = "&api_key=";
    private final static String ASCII2D_URL = "https://ascii2d.net/search/uri";
    private final static String URL = "uri=";
    private Random random = new Random();
    private int length;
    private List<String> tokens;
    @Value("${saucenao.token.path}")
    private String path;

    @PostConstruct
    public void init() throws IOException {
        File json = new File(path);
        tokens = objectMapper.readValue(json, new TypeReference<ArrayList<String>>() {
        });
        length = tokens.size();
    }

    public CompletableFuture<SaucenaoResponse> searchBySaucenao(String url) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(SAUCENAO_URL_1 + url + SAUCENAO_URL_2 + tokens.get(random.nextInt(length))))
                .GET()
                .build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(s -> {
            SaucenaoResponse saucenaoResponse= null;
            try {
                saucenaoResponse = objectMapper.readValue(s.body(), new TypeReference<SaucenaoResponse>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
            return saucenaoResponse;
        });
    }

    public CompletableFuture<HttpResponse<String>> searchByAscii2D(String url) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(ASCII2D_URL))
                .POST(HttpRequest.BodyPublishers.ofString(URL+ URLDecoder.decode(url, StandardCharsets.UTF_8)))
                .build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }


}
