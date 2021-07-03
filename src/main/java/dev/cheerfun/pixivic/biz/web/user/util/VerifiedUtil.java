package dev.cheerfun.pixivic.biz.web.user.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.user.dto.VerifiedResponseDTO;
import dev.cheerfun.pixivic.biz.web.user.dto.VerifiedResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/26 3:29 PM
 * @description VerifiedUtil
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VerifiedUtil {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    @Value("${verifiedAPI.url}")
    private String url;
    @Value("${verifiedAPI.secretId}")
    private String secretId;
    @Value("${verifiedAPI.secretKey}")
    private String secretKey;

    public VerifiedResponseResult verifyUser(String name, String idCard, String phone) throws NoSuchAlgorithmException, InvalidKeyException, IOException, InterruptedException {
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String datetime = sdf.format(cd.getTime());
        // 签名
        String auth = calcAuthorization("market", secretId, secretKey, datetime);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("X-Source", "market")
                .header("X-Date", datetime)
                .header("Authorization", auth)
                .uri(URI.create(url + idCard + "&mobile=" + phone + "&name=" + name))
                .GET()
                .build();
        try {
            String body = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
            VerifiedResponseDTO verifiedResponseDTO = objectMapper.readValue(body, new TypeReference<VerifiedResponseDTO>() {
            });
            if ("0".equals(verifiedResponseDTO.getCode())) {
                return verifiedResponseDTO.getResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String calcAuthorization(String source, String secretId, String secretKey, String datetime)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String signStr = "x-date: " + datetime + "\n" + "x-source: " + source;
        Mac mac = Mac.getInstance("HmacSHA1");
        Key sKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), mac.getAlgorithm());
        mac.init(sKey);
        byte[] hash = mac.doFinal(signStr.getBytes(StandardCharsets.UTF_8));
        String sig = Base64.getEncoder().encodeToString(hash);
        return "hmac id=\"" + secretId + "\", algorithm=\"hmac-sha1\", headers=\"x-date x-source\", signature=\"" + sig + "\"";
    }

}
