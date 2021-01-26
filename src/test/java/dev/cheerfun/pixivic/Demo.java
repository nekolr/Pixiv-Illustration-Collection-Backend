package dev.cheerfun.pixivic;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/26 1:42 PM
 * @description Demo
 */
class Demo {
    public static String calcAuthorization(String source, String secretId, String secretKey, String datetime)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String signStr = "x-date: " + datetime + "\n" + "x-source: " + source;
        Mac mac = Mac.getInstance("HmacSHA1");
        Key sKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), mac.getAlgorithm());
        mac.init(sKey);
        byte[] hash = mac.doFinal(signStr.getBytes("UTF-8"));
        String sig = Base64.getEncoder().encodeToString(hash);

        String auth = "hmac id=\"" + secretId + "\", algorithm=\"hmac-sha1\", headers=\"x-date x-source\", signature=\"" + sig + "\"";
        return auth;
    }

    public static String urlencode(Map<?, ?> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    URLEncoder.encode(entry.getKey().toString(), "UTF-8"),
                    URLEncoder.encode(entry.getValue().toString(), "UTF-8")
            ));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException, InterruptedException {
        //云市场分配的密钥Id
        String secretId = "AKID9GO4RsrMlSTTPnf3Yc7aH1ndWddDaKHppcQ";
        //云市场分配的密钥Key
        String secretKey = "iNRRsAbupPjZwcjrmjCm1dMqh6t07v8uLs4Cml3W";
        String source = "market";
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String datetime = sdf.format(cd.getTime());
        // 签名
        String auth = calcAuthorization(source, secretId, secretKey, datetime);
        // 查询参数
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("idcard", "350723199704010412");
        queryParams.put("mobile", "15507838938");
        queryParams.put("name", "林豪");
        // body参数
        Map<String, String> bodyParams = new HashMap<String, String>();

        // url参数拼接
        String url = "https://service-mlmeeiob-1256140209.ap-shanghai.apigateway.myqcloud.com/release/mobile/verify_real_name";
        if (!queryParams.isEmpty()) {
            url += "?" + urlencode(queryParams);
        }

        HttpClient build = HttpClient.newBuilder().build();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("X-Source", source)
                .header("X-Date", datetime)
                .header("Authorization", auth)
                .uri(URI.create(url))
                .GET()
                .build();

        System.out.println(build.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body());
    }
}