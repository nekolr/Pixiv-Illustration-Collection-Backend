package dev.cheerfun.pixivic.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 13:48
 * @description 异步线程执行任务配置
 */
@EnableAsync
@Configuration
public class AsyncExecutor {

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 10;
    /**
     * 最大线程数
     */
    private static final int MAX_POOL_SIZE = 50;
    /**
     * 工作队列容量
     */
    private static final int QUEUE_CAPACITY = 1000;
    /**
     * 线程名称前缀
     */
    private static final String THREAD_NAME_PREFIX = "asyncPixPool-";

    @Bean(value = "asyncExecutor")
    public void asyncPixExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        //当任务添加到线程池中被拒绝时，线程池将丢弃被拒绝的任务。
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();
    }
}
