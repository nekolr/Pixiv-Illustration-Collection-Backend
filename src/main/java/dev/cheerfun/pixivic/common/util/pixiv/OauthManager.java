package dev.cheerfun.pixivic.common.util.pixiv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.domain.PixivUser;
import dev.cheerfun.pixivic.common.util.dto.pixiv.OathRespBody;
import dev.cheerfun.pixivic.common.util.json.JsonBodyHandler;
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

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OauthManager {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${pixiv.oauth.config}")
    private String path;
    private Random random = new Random();

    @Getter
    private volatile List<PixivUser> pixivUserList;
    private volatile int pixivUserSize;

    //失败队列，由于只会有一个线程访问所以不需要可见
    private ArrayList<PixivUser> refreshErrorList;

    @PostConstruct
    private void init() throws IOException {
        //读取账号信息
        File json = new File(path);
        pixivUserList = objectMapper.readValue(json, new TypeReference<ArrayList<PixivUser>>() {
        });
        //账号初始化
        refreshAccessToken();
        pixivUserSize = pixivUserList.size();
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void refreshAccessToken() {
        System.out.println("开始刷新帐号池");
        for (int i = 0; i < pixivUserSize; i++) {
            if (!refresh(pixivUserList.get(i))) {
                refreshErrorList.add(pixivUserList.get(i));
            }
        }

        for (int i = 0; i < refreshErrorList.size(); i++) {
            if (!refresh(refreshErrorList.get(i))) {
                refreshErrorList.add(refreshErrorList.get(i));
            }
        }
        System.out.println("帐号池刷新完毕");
    }

    private boolean refresh(PixivUser pixivUser) {
        long start = System.currentTimeMillis();
        //自选10秒
        while ((System.currentTimeMillis() - start) < 10 * 1000) {
            try {
                HttpRequest.Builder uri = HttpRequest.newBuilder()
                        .uri(URI.create("https://proxy.pixivic.com:23334/auth/token"));
                RequestUtil.decorateHeader(uri);
                HttpRequest httpRequest = uri.POST(HttpRequest.BodyPublishers.ofString(pixivUser.getRequestBody()))
                        .build();
                OathRespBody body;
                body = (OathRespBody) httpClient.send(httpRequest, JsonBodyHandler.jsonBodyHandler(OathRespBody.class)).body();
                pixivUser.refresh(body);
            } catch (Exception e) {
                pixivUser.refreshError();
                System.err.println("账号" + pixivUser.getUsername() + "刷新token失败");
                e.printStackTrace();
                continue;
            }
            return true;
        }
        return false;
    }

    public int getRandomOauthIndex() {
        long start = System.currentTimeMillis();
        //自旋获取
        while (System.currentTimeMillis() - start < 1000 * 15) {
            int i = random.nextInt(pixivUserSize);
            PixivUser pixivUser = pixivUserList.get(i);
            //token不为空且令牌桶足够
            if (pixivUser.getAccessToken() != null && pixivUser.getBucket().tryConsumeAndReturnRemaining(1).isConsumed()) {
                return i;
            }
        }
        refreshAccessToken();
        throw new RuntimeException("获取token失败，开始重新刷新");
    }
}
