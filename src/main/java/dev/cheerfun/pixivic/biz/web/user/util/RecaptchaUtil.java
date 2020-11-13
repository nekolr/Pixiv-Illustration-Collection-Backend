package dev.cheerfun.pixivic.biz.web.user.util;

import dev.cheerfun.pixivic.biz.web.common.exception.UserCommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/11/13 11:17 PM
 * @description RecaptchaUtil
 */
@Component
public class RecaptchaUtil {
    @Value("${recaptcha.secret}")
    private String secret;
    @Autowired
    private HttpClient httpClient;

    public Boolean check(String gRecaptchaResponse) throws IOException, InterruptedException {
        if (gRecaptchaResponse == null) {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "缺少谷歌验证");
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.recaptcha.net/recaptcha/api/siteverify")).POST(HttpRequest.BodyPublishers.ofString("response=" + gRecaptchaResponse + "&secret=" + secret)).build();
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        if (body.contains("true")) {
            return true;
        } else {
            if (body.contains("missing-input-response")) {
                throw new UserCommonException(HttpStatus.BAD_REQUEST, "缺少谷歌验证");
            }
            if (body.contains("timeout-or-duplicate")) {
                throw new UserCommonException(HttpStatus.BAD_REQUEST, "谷歌验证超时或重复");
            }
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "谷歌验证无效");
        }
    }
}
