package dev.cheerfun.pixivic.web.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-06-28 23:56
 * @description mybatis-plus配置类
 */
@Configuration
@MapperScan("dev.cheerfun.pixivic.web.ArtistMapper")
public class MybatisPlusConfig {
    /**
     * 分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
    /**
     * SQL执行效率插件
     * @return
     */
    @Bean
    @Profile({"dev"})
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }
}
