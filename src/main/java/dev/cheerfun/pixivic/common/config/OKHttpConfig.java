package dev.cheerfun.pixivic.common.config;

import dev.cheerfun.pixivic.common.util.pixiv.OauthManager;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/5/16 10:09 下午
 * @description OKHttpConfig
 */
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OKHttpConfig {
    private final OauthManager oauthManager;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(
                        chain -> {
                            String[] hash = RequestUtil.gethash();
                            Request request = chain.request().newBuilder()
                                    .header("Artist-Agent", "PixivAndroidApp/5.0.93 (Android 5.1; m2)")
                                    .header("Content-Type", "application/x-www-form-urlencoded")
                                    .header("App-OS", "android")
                                    .header("App-OS-Version", "5.1")
                                    .header("App-Version", "5.0.93")
                                    .header("Accept-Language", "zh_CN")
                                    .header("X-Client-Hash", hash[1])
                                    .header("X-Client-Time", hash[0])
                                    .header("Authorization", "Bearer " + oauthManager.getRandomPixivUser().getAccessToken())
                                    .build();
                            return chain.proceed(request);
                        })
                //  .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
                .build();
    }

}
