package dev.cheerfun.pixivic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

/**
 * @author OysterQAQ
 * @version 2.0
 * @date 2019-08-14
 * @description pixivic项目启动类
 */
@SpringBootApplication
@EnableAsync(mode = AdviceMode.ASPECTJ)
@EnableCaching(mode = AdviceMode.ASPECTJ)
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
@EnableJpaRepositories(basePackages = {"dev.cheerfun.pixivic.biz.web.admin.repository", "dev.cheerfun.pixivic.biz.web.app.repository"})
public class Application {
    public static void main(String[] args) {
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Referer");
        //System.setProperty("jdk.httpclient.keepalive.timeout", "1000");
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Connection");
        System.setProperty("jdk.httpclient.redirects.retrylimit", "10");
        System.setProperty("jdk.httpclient.disableRetryConnect", "true");
        System.setProperty("jdk.httpclient.enableAllMethodRetry", "true");
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Content-Length");
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");//取消主机名验证
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow", "|{}");
        SpringApplication.run(Application.class, args);
    }
}
