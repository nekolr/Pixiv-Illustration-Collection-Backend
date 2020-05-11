package dev.cheerfun.pixivic.common.util.pixiv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.domain.Oauth;
import dev.cheerfun.pixivic.common.util.dto.pixiv.OathRespBody;
import dev.cheerfun.pixivic.common.util.json.JsonBodyHandler;
import io.github.bucket4j.ConsumptionProbe;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
final public class OauthManager {
    private static ReentrantLock lock = new ReentrantLock();
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    @Value("${pixiv.oauth.config}")
    private String path;
    private Random random = new Random();
    private int oauthListSize;

    @Getter
    private volatile List<Oauth> oauths;

    @PostConstruct
    private void init() throws IOException {
        //读取账号信息
        File json = new File(path);
        oauths = objectMapper.readValue(json, new TypeReference<ArrayList<Oauth>>() {
        });
        //账号初始化
        System.out.println("开始初始化帐号池");
        CompletableFuture.runAsync(() -> oauths.stream().parallel().forEach(this::oauthInit));
        System.out.println("帐号池初始化完毕");
        oauthListSize = oauths.size();
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void refreshAccessToken() {
        System.out.println("开始刷新帐号池");
        oauths.stream().parallel().forEach(e -> {
            if ("password".equals(e.getGrantType())) {
                oauthInit(e);
            } else {
                refresh(e);
            }
        });
        System.out.println("帐号池刷新完毕");
    }

    private void oauthInit(Oauth oauth) {
        refresh(oauth);
        oauth.setGrantType("refresh_token");
    }

    private void refresh(Oauth oauth) {
        HttpRequest.Builder uri = HttpRequest.newBuilder()
                .uri(URI.create("https://proxy.pixivic.com:23334/auth/token"));
        RequestUtil.decorateHeader(uri);
        HttpRequest httpRequest = uri.POST(HttpRequest.BodyPublishers.ofString(oauth.getRequestBody()))
                .build();
        OathRespBody body = null;
        try {
            body = (OathRespBody) httpClient.send(httpRequest, JsonBodyHandler.jsonBodyHandler(OathRespBody.class)).body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

        }
        assert body != null;
        oauth.refresh(body);
        System.out.println(oauth.getAccessToken());
    }

    public int getRandomOauthIndex() {
        try {
            lock.lock();
            while (true) {
                int i = random.nextInt(oauthListSize);
                Oauth oauth = oauths.get(i);
                if (oauth.getAccessToken() == null) {
                    continue;
                }
                if (!oauth.getIsBan()) {
                    ConsumptionProbe consumptionProbe = oauth.getBucket().tryConsumeAndReturnRemaining(1);
                    if (consumptionProbe.isConsumed()) {
                        return i;
                    }
                }
                if (oauths.stream().noneMatch(Oauth::getIsBan)) {
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            lock.unlock();
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
