package dev.cheerfun.pixivic.common.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("httpclient-pool-%d").build();
        return new ThreadPoolExecutor(
                4,
                40,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "httpclientExecutorService")
    public ExecutorService httpclientExecutorService() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("httpclient-pool-%d").build();
        return new ThreadPoolExecutor(
                4,
                10,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

}
