package dev.cheerfun.pixivic.biz.cibr.config;

import io.milvus.client.ConnectParam;
import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusGrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/6 11:06 PM
 * @description MilvusConfig
 */
//@Configuration
public class MilvusConfig {
    @Value("${milvus.host}")
    private String host;
    @Value("${milvus.port}")
    private Integer port;

    @Bean
    public MilvusClient milvusClient() {
        ConnectParam connectParam = new ConnectParam.Builder().withHost(host).withPort(port).build();
        return new MilvusGrpcClient(connectParam);
    }

}
