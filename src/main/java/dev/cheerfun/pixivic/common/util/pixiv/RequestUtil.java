package dev.cheerfun.pixivic.common.util.pixiv;

import dev.cheerfun.pixivic.common.util.JsonBodyHandler;
import dev.cheerfun.pixivic.crawler.model.Oauth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
final public class RequestUtil {
    //@Resource(name = "httpClientWithProxy")
    private final HttpClient httpClient;
    private final OauthUtil oauthUtil;

    public static String getPostEntity(Map<String, String> param) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
        Map.Entry<String, String> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return stringBuilder.toString();
    }

    public CompletableFuture<String> getJson(String url) {
        HttpRequest.Builder uri = HttpRequest.newBuilder()
                .uri(URI.create(url));
        decorateHeader(uri);
        int randomOauthIndex = oauthUtil.getRandomOauthIndex();
        Oauth oauth = oauthUtil.getOauths().get(randomOauthIndex);
        HttpRequest getRank = uri
                .header("Authorization", "Bearer " + oauth.getAccess_token())
                .GET()
                .build();
        return httpClient.sendAsync(getRank, HttpResponse.BodyHandlers.ofString()).thenApply(resp -> {
            int code = resp.statusCode();
            //System.out.println(resp.body().length());
            if (code == 403) {
                oauthUtil.ban(randomOauthIndex);
                return "false";
            }
            return resp.body();
        });
    }

    public Object getJsonSync(String url, Class target) {
        HttpRequest.Builder uri = HttpRequest.newBuilder()
                .uri(URI.create(url));
        decorateHeader(uri);
        int randomOauthIndex = oauthUtil.getRandomOauthIndex();
        Oauth oauth = oauthUtil.getOauths().get(randomOauthIndex);
        HttpRequest getRank = uri
                .header("Authorization", "Bearer " + oauth.getAccess_token())
                .GET()
                .build();
        try {
            return httpClient.send(getRank, JsonBodyHandler.jsonBodyHandler(target)).body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

/*
    public CompletableFuture<String> getJsonAsync(String url) {
        HttpRequest.Builder uri = HttpRequest.newBuilder()
                .uri(URI.create(url));
        decorateHeader(uri);
        int randomOauthIndex = oauthUtil.getRandomOauthIndex();
        Oauth oauth = oauthUtil.getOauths().get(randomOauthIndex);
        HttpRequest getRank = uri
                .header("Authorization", "Bearer " + oauth.getAccess_token())
                .GET()
                .build();
        return httpClient.sendAsync(getRank, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }
*/

    private static String[] gethash() {
        SimpleDateFormat simpleDateFormat;
        String fortmat = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
        simpleDateFormat = new SimpleDateFormat(fortmat, Locale.US);
        Date date = new Date();
        String time = simpleDateFormat.format(date);
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String seed = time + "28c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c";
        assert md5 != null;
        byte[] digest = md5.digest(seed.getBytes());
        StringBuilder hash = new StringBuilder();
        for (byte b : digest) hash.append(String.format("%02x", b));
        return new String[]{time, hash.toString()};
    }

    public static void decorateHeader(HttpRequest.Builder builder) {
        String[] hash = gethash();
        builder.header("Artist-Agent", "PixivAndroidApp/5.0.93 (Android 5.1; m2)")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("App-OS", "android")
                .header("App-OS-Version", "5.1")
                .header("App-Version", "5.0.93")
                .header("Accept-Language", "zh_CN")
                .header("X-Client-Hash", hash[1])
                .header("X-Client-Time", hash[0]);
    }
}
