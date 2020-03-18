package dev.cheerfun.pixivic.biz.notify.config;

import dev.cheerfun.pixivic.biz.notify.constant.NotifyObjectType;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 2:50 下午
 * @description MessagingRabbitmqConfig
 */
@Configuration
public class MessagingRabbitmqConfig {
    @Bean("commentQueue")
    public Queue commentQueue() {
        return new Queue(NotifyObjectType.COMMENT);
    }

    @Bean("illustQueue")
    public Queue illustQueue() {
        return new Queue(NotifyObjectType.ILLUST);
    }

    @Bean("collectionQueue")
    public Queue collectionQueue() {
        return new Queue(NotifyObjectType.COLLECTION);
    }

}
