package dev.cheerfun.pixivic.web.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 22:21
 * @description PathMatchingConfigurationAdapter
 */
@Configuration
@EnableWebMvc
public class PathMatchingConfigurationAdapter extends WebMvcConfigurerAdapter {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }
}
