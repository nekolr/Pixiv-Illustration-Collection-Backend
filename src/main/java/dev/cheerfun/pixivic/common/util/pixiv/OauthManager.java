package dev.cheerfun.pixivic.common.util.pixiv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.domain.PixivUser;
import dev.cheerfun.pixivic.common.util.pixiv.mapper.PixivAccountMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OauthManager {

    private final HttpClient httpClient;
    private final PixivAccountMapper pixivAccountMapper;
    private final ObjectMapper objectMapper;
    private final ExecutorService pixivAccountManagerExecutorService;
    @Getter
    private volatile List<PixivUser> pixivUserList;
    private volatile int pixivUserSize;
    //失败队列，由于只会有一个线程访问所以不需要可见
    private Set<Integer> refreshErrorSet;

    @PostConstruct
    private void init() throws IOException {
        //数据库读取账号信息
        //马上刷新，异步写回数据库
        //读取账号信息
        refreshErrorSet = new HashSet<>();
        pixivUserList = pixivAccountMapper.queryPixivUser();
        //账号初始化
        pixivUserSize = pixivUserList.size();
        refresh();
    }

    public void refresh() {
        pixivAccountManagerExecutorService.submit(() -> {
            while (true) {
                refreshAccessToken();
                Thread.sleep(30 *
                        60 *
                        1000);
            }
        });
    }

    public void refreshAccessToken() {
        long start = System.currentTimeMillis();
        System.out.println("开始刷新帐号池" + start);
        for (int i = 0; i < pixivUserSize; i++) {
            if (!refresh(pixivUserList.get(i))) {
                refreshErrorSet.add(i);
            } else {
                refreshErrorSet.remove(i);
            }
        }
        refreshErrorSet.removeIf(e -> refresh(pixivUserList.get(e)));
        System.out.println("帐号池刷新完毕,耗时" + (System.currentTimeMillis() - start) / 1000 + "秒");
    }

    private boolean refresh(PixivUser pixivUser) {
        long start = System.currentTimeMillis();
        //自旋10秒
        while ((System.currentTimeMillis() - start) < 60 * 1000) {
            try {
                HttpRequest.Builder uri = HttpRequest.newBuilder()
                        .uri(URI.create("http://proxy.pixivic.com:23334/auth/token"));
                RequestUtil.decorateHeader(uri);
                HttpRequest httpRequest = uri.POST(HttpRequest.BodyPublishers.ofString(pixivUser.getRequestBody()))
                        .build();
                OathRespBody body;
                String body1 = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
                System.out.println(body1);
                //body = (OathRespBody) httpClient.send(httpRequest, JsonBodyHandler.jsonBodyHandler(OathRespBody.class)).body();
                body = objectMapper.readValue(body1, new TypeReference<OathRespBody>() {
                });
                if (pixivUser.refresh(body)) {
                    System.out.println(pixivUser.getUsername() + "账号刷新成功");
                    saveRefreshTokenToDb(pixivUser);
                    return true;
                }
            } catch (Exception e) {
                pixivUser.ban();
                System.err.println("账号" + pixivUser.getUsername() + "刷新token失败");
                e.printStackTrace();
                continue;
            }
            return false;
        }
        return false;
    }

    public void ban(PixivUser pixivUser) {
        pixivUser.ban();
    }

    public PixivUser getRandomPixivUser() {
        long start = System.currentTimeMillis();
        //自旋获取
        while (System.currentTimeMillis() - start < 1000 * 15) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int i = random.nextInt(pixivUserSize);
            PixivUser pixivUser = pixivUserList.get(i);
            //token不为空且令牌桶足够
            if (pixivUser.getAccessToken() != null && pixivUser.getBucket().tryConsumeAndReturnRemaining(1).isConsumed()) {
                return pixivUser;
            }
        }
        //超时手动刷新
        //refreshAccessToken();
        throw new RuntimeException("获取token失败，开始重新刷新");
    }

    @Async()
    void saveRefreshTokenToDb(PixivUser pixivUser) {
        pixivAccountMapper.saveRefreshTokenToDb(pixivUser.getId(), pixivUser.getRefreshToken());
    }

}
