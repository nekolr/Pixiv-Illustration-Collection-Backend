package dev.cheerfun.pixivic.biz.crawler.pixiv.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.remote.PixivService;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/5/16 10:33 下午
 * @description RetrofitConfig
 */
@Configuration
public class PixivRetrofitConfig {
    @Bean
    public Retrofit retrofit(OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        return new Retrofit.Builder()
                .baseUrl("https://proxy.pixivic.com:23334")
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
    }

    @Bean
    public PixivService pixivService(Retrofit retrofit) {
        PixivService pixivService = retrofit.create(PixivService.class);
        return pixivService;
    }
}
