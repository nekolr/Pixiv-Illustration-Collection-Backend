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

@Data
public class Oauth {
    public final static String CLIENT_ID = "KzEZED7aC0vird8jWyHM38mXjNTY";
    public final static String CLIENT_SECRET = "W9JZoJe00qPvJsiyCGT3CCtC6ZUtdpKpzMbNlUGP";
    private volatile String accessToken;
    private String deviceToken;
    private String refreshToken;
    private String grantType;
    private String username;
    private String password;
    private volatile Boolean isBan = false;
    private long banAt;
    private Bucket bucket;

    {
        deviceToken = "fb12e7c1945000850deb5f7001c02745";
        refreshToken="";
        grantType = "password";
        bucket = Bucket4j.builder()
                .addLimit(Bandwidth.classic(800, Refill.intervally(800, Duration.ofMinutes(10))))
                .build();
    }

    public String getRequestBody() {
        Map<String, String> paramMap = new HashMap<>(
                Map.of("client_id", CLIENT_ID, "client_secret", CLIENT_SECRET, "grant_type", grantType
                        , "username", username, "password", password, "device_token", deviceToken,"refresh_token", refreshToken
                        , "get_secure_url", "true"));
        return RequestUtil.getPostEntity(paramMap);
    }

    public void refresh(OathRespBody responseBody) {
        OathResp response = responseBody.getResponse();
        accessToken = response.getAccessToken();
        deviceToken = response.getDeviceToken();
        refreshToken = response.getRefreshToken();
    }

}
