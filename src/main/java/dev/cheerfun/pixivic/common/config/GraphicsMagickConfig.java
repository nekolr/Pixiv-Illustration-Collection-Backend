package dev.cheerfun.pixivic.common.config;

import lombok.extern.slf4j.Slf4j;
import org.gm4java.engine.support.GMConnectionPoolConfig;
import org.gm4java.engine.support.PooledGMService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/27 4:00 下午
 * @description GraphicsMagickConfig
 */
//@Configuration
@Slf4j
public class GraphicsMagickConfig {
    @Bean
    public PooledGMService pooledGMService(@Value("${graphicsMagick.path}") String GMPath) {
        try {
            GMConnectionPoolConfig gmConnectionPoolConfig = new GMConnectionPoolConfig();
            gmConnectionPoolConfig.setGMPath(GMPath);
            return new PooledGMService(gmConnectionPoolConfig);
        } catch (Exception exception) {
            log.error("GraphicsMagick初始化很失败");
            exception.printStackTrace();
        }
        return null;
    }
}
