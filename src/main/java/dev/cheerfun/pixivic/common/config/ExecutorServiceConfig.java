package dev.cheerfun.pixivic.common.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/5/15 11:55 上午
 * @description ExecutorServiceConfig
 */
@Configuration
public class ExecutorServiceConfig {
    @Bean(name = "executorService")
    public ExecutorService executorService() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("common-pool-%d").build();
        return new ThreadPoolExecutor(
                4,
                40,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2048 * 1000),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "crawlerExecutorService")
    public ExecutorService crawlerExecutorService() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("crawler-pool-%d").build();
        return new ThreadPoolExecutor(
                50,
                50,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2048 * 1000),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "pixivAccountManagerExecutorService")
    public ExecutorService pixivAccountManagerExecutorService() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("pixivAccountManager-pool-%d").build();
        return new ThreadPoolExecutor(
                4,
                8,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2048 * 1000),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "mailExecutorService")
    public ExecutorService mailExecutorService() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("mail-pool-%d").build();
        return new ThreadPoolExecutor(
                30,
                30,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2048 * 1000),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "messageExecutorService")
    public ExecutorService messageExecutorService() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("message-pool-%d").build();
        return new ThreadPoolExecutor(
                5,
                5,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2048 * 1000),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "recommendDownGradeExecutorService")
    public ExecutorService recommendDownGradeExecutorService() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("recommendDowngrade-pool-%d").build();
        return new ThreadPoolExecutor(
                4,
                40,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2048 * 1000),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "saveToDBExecutorService")
    public ExecutorService saveToDBExecutorService() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("saveToDB-pool-%d").build();
        return new ThreadPoolExecutor(
                4,
                50,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2048 * 1000),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "httpclientExecutorService")
    public ExecutorService httpclientExecutorService() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("httpclient-pool-%d").build();
        return new ThreadPoolExecutor(
                10,
                10,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2048 * 1000),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

}
