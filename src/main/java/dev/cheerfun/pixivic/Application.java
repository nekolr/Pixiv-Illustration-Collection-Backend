package dev.cheerfun.pixivic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author OysterQAQ
 * @version 2.0
 * @date 2019-08-14
 * @description pixivic项目启动类
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching(mode = AdviceMode.ASPECTJ)
@EnableTransactionManagement
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
public class Application {
    public static void main(String[] args) {
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Referer");
        System.setProperty("jdk.httpclient.redirects.retrylimit", "30");
        System.setProperty("jdk.httpclient.disableRetryConnect", "true");
        System.setProperty("jdk.httpclient.enableAllMethodRetry", "true");
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Content-Length");
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");//取消主机名验证
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow", "|{}");
        SpringApplication.run(Application.class, args);
    }
}
