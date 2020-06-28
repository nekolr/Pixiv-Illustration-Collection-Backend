package dev.cheerfun.pixivic.common.config;

import com.thoughtworks.xstream.XStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/6/28 5:16 下午
 * @description XStreamConfig
 */
@Configuration
public class XStreamConfig {
    @Bean
    public XStream xStream() {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }
}
