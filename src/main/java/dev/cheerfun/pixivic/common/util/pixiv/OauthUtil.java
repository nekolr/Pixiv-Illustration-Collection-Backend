package dev.cheerfun.pixivic.common.util.pixiv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.crawler.model.Oauth;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
final public class OauthUtil {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper ;
    @Value("${pixiv.oauth.client_id}")
    private String client_id;
    @Value("${pixiv.oauth.client_secret}")
    private String client_secret;
    @Value("${pixiv.oauth.refresh_token}")
    private String refresh_token;
    @Value("${pixiv.oauth.device_token}")
    private String device_token;
    @Value("${pixiv.oauth.config}")
    private String path;

    private Random random = new Random();
    private static ReentrantLock lock = new ReentrantLock();
    private int length;

    @Getter
    private volatile List<Oauth> oauths;

    @PostConstruct
    private void init() throws IOException {
        //读取账号信息
        File json = new File(path);
        oauths = objectMapper.readValue(json, new TypeReference<ArrayList<Oauth>>() {
        });
        //账号初始化
        oauths.stream().parallel().forEach(this::oauthInit);
        oauths.forEach(oauth -> System.out.println(oauth.getAccess_token()));
        length = oauths.size();

    }

    private void oauthInit(Oauth oauth) {
        Map<String, String> paramMap = new HashMap<>(
                Map.of("client_id", client_id, "client_secret", client_secret, "grant_type", "password"
                        , "username", oauth.getUsername(), "password", oauth.getPassword(), "device_token", device_token
                        , "get_secure_url", "true"));
        oauth.setAccess_token(refreshToken(RequestUtil.getPostEntity(paramMap)));
        //paramMap.replace("grant_type", "refresh_token");
        //paramMap.put("refresh_token", refresh_token);
        oauth.setParam(RequestUtil.getPostEntity(paramMap));
    }

    private String refreshToken(String param) {
        HttpRequest.Builder uri = HttpRequest.newBuilder()
                .uri(URI.create("https://proxy.pixivic.com:23334/auth/token"));
        RequestUtil.decorateHeader(uri);
        HttpRequest oauth = uri.POST(HttpRequest.BodyPublishers.ofString(param))
                .build();
        String body = null;
        try {
            body = httpClient.send(oauth, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assert body != null;
        int index = body.indexOf("\"access_token\":\"");
        return body.substring(index + 16, index + 59);
    }

    public void refreshAccess_token() {
        oauths.stream().parallel().forEach(oauth -> refreshToken(oauth.getParam()));
    }

    public int getRandomOauthIndex() {
        while (true) {
            int i = random.nextInt(length);
            if (oauths.get(i).getIsBan()) {
                if (oauths.stream().noneMatch(Oauth::getIsBan)) {
                    try {
                        Thread.sleep(1000 * 60);
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return i;
        }
    }

    void ban(int index) {
        Oauth oauth = oauths.get(index);
        try {
            lock.lock();
            oauth.setIsBan(true);
            oauth.setBanAt(System.currentTimeMillis());
        } finally {
            lock.unlock();
        }
    }

    @Scheduled(initialDelay = 1000 * 60 * 10, fixedRate = 5000)
    public void scanAndResetIsban() {
        oauths.stream().parallel().filter(Oauth::getIsBan).forEach(oauth -> {
            if (System.currentTimeMillis() - oauth.getBanAt() > 1000 * 60 * 10)
                try {
                    lock.lock();
                    oauth.setIsBan(false);
                } finally {
                    lock.unlock();
                }
        });
    }

}
