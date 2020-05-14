package dev.cheerfun.pixivic.biz.crawler.pixiv.domain;

import dev.cheerfun.pixivic.common.util.dto.pixiv.OathResp;
import dev.cheerfun.pixivic.common.util.dto.pixiv.OathRespBody;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.Data;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Data
public class PixivUser {
    public final static String CLIENT_ID = "KzEZED7aC0vird8jWyHM38mXjNTY";
    public final static String CLIENT_SECRET = "W9JZoJe00qPvJsiyCGT3CCtC6ZUtdpKpzMbNlUGP";
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
    final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private volatile String accessToken;
    private String deviceToken;
    private String refreshToken;
    private String grantType;
    private String username;
    private String password;
    private Bucket bucket;
    private volatile Integer isBan;

    {
        deviceToken = "fb12e7c1945000850deb5f7001c02745";
        refreshToken = "";
        grantType = "password";
        bucket = Bucket4j.builder()
                .addLimit(Bandwidth.classic(800, Refill.intervally(800, Duration.ofMinutes(10))))
                .build();
    }

    public String getRequestBody() {
        Map<String, String> paramMap = new HashMap<>(
                Map.of("client_id", CLIENT_ID, "client_secret", CLIENT_SECRET, "grant_type", grantType
                        , "username", username, "password", password, "device_token", deviceToken, "refresh_token", refreshToken
                        , "get_secure_url", "true"));
        return RequestUtil.getPostEntity(paramMap);
    }

    public String getAccessToken() {
        readLock.lock();
        try {
            return accessToken;
        } finally {
            readLock.unlock();
        }

    }

    public void refresh(OathRespBody responseBody) {
        writeLock.lock();
        try {
            OathResp response = responseBody.getResponse();
            accessToken = response.getAccessToken();
            deviceToken = response.getDeviceToken();
            refreshToken = response.getRefreshToken();
            grantType = "refresh_token";
            isBan = 0;
        } finally {
            writeLock.unlock();
        }

    }

    public void refreshError() {
        writeLock.lock();
        try {
            accessToken = null;
            grantType = "password";
            isBan = 1;
        } finally {
            writeLock.unlock();
        }
    }

}
