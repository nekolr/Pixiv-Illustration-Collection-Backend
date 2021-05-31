package dev.cheerfun.pixivic.biz.dns.config;

import com.jdcloud.sdk.auth.CredentialsProvider;
import com.jdcloud.sdk.auth.StaticCredentialsProvider;
import com.jdcloud.sdk.http.HttpRequestConfig;
import com.jdcloud.sdk.http.Protocol;
import com.jdcloud.sdk.service.clouddnsservice.client.ClouddnsserviceClient;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/31 11:19 PM
 * @description JDDNSConfig
 */
@AllArgsConstructor
@Configuration
public class JDDNSConfig {
    @Bean
    public ClouddnsserviceClient clouddnsserviceClient(@Value("${jdcloud.accessKeyId}")
                                                               String accessKeyId, @Value("${jdcloud.secretAccessKey}")
                                                               String secretAccessKey) {
        CredentialsProvider credentialsProvider = new StaticCredentialsProvider(accessKeyId, secretAccessKey);
        return ClouddnsserviceClient.builder()
                .credentialsProvider(credentialsProvider)
                .httpRequestConfig(new HttpRequestConfig.Builder().protocol(Protocol.HTTPS).build()) //默认为HTTPS
                .build();
    }
}
