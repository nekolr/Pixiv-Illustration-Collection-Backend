package dev.cheerfun.pixivic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-06-28 23:03
 * @description pixivic项目启动类
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Referer");
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Content-Length");
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");//取消主机名验证
        SpringApplication.run(Application.class,args);
    }
}
