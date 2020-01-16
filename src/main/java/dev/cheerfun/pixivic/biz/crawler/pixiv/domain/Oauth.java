package dev.cheerfun.pixivic.biz.crawler.pixiv.domain;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.Data;

import java.time.Duration;

@Data
public class Oauth {
    private volatile String accessToken;
    private String username;
    private String password;
    private volatile Boolean isBan = false;
    private String param;
    private long banAt;
    private Bucket bucket;

    {
        bucket = Bucket4j.builder()
                .addLimit(Bandwidth.classic(800, Refill.intervally(800, Duration.ofMinutes(10))))
                .build();
    }

}
