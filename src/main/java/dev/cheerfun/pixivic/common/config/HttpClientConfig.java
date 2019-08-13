package dev.cheerfun.pixivic.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/01 9:25
 * @description HttpClientConfig
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient httpClient() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCertificates = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null; // Not relevant.
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                // TODO Auto-generated method stub
            }
        }};
        SSLParameters sslParams = new SSLParameters();
        sslParams.setEndpointIdentificationAlgorithm("");
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCertificates, new SecureRandom());
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .sslParameters(sslParams)
                .sslContext(sc)
              //  .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 9999)))
                .executor(Executors.newFixedThreadPool(6))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }
}
