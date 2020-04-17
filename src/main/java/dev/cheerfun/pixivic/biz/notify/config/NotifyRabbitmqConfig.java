package dev.cheerfun.pixivic.biz.notify.config;

import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
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
public class NotifyRabbitmqConfig {

    @Bean("notifyExchange")
    DirectExchange notifyExchange() {
        return new DirectExchange("notifyExchange");//配置广播路由器
    }

    @Bean("commentNotifyQueue")
    public Queue commentNotifyQueue() {
        return new Queue(ObjectType.COMMENT);
    }

    @Bean("illustNotifyQueue")
    public Queue illustNotifyQueue() {
        return new Queue(ObjectType.ILLUST);
    }

    @Bean("collectionNotifyQueue")
    public Queue collectionNotifyQueue() {
        return new Queue(ObjectType.COLLECTION);
    }

    @Bean
    public Binding notifyExchangeBindCommentQueue(Queue commentNotifyQueue) {
        return BindingBuilder.bind(commentNotifyQueue).to(notifyExchange()).with(ObjectType.COMMENT);
    }

    @Bean
    public Binding notifyExchangeBindIllustQueue(Queue illustNotifyQueue) {
        return BindingBuilder.bind(illustNotifyQueue).to(notifyExchange()).with(ObjectType.ILLUST);
    }

    @Bean
    public Binding notifyExchangeBindCollectionQueue(Queue collectionNotifyQueue) {
        return BindingBuilder.bind(collectionNotifyQueue).to(notifyExchange()).with(ObjectType.COLLECTION);
    }

}
