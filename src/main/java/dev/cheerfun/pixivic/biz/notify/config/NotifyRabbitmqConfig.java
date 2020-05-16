package dev.cheerfun.pixivic.biz.notify.config;

import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import org.springframework.amqp.core.*;
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
        return new DirectExchange("notifyExchange");
    }

    @Bean
    public Binding bindFanoutExchange(FanoutExchange fanoutExchange, DirectExchange notifyExchange) {
        return BindingBuilder.bind(notifyExchange).to(fanoutExchange);
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
    public Binding notifyExchangeBindCommentQueue(Queue commentNotifyQueue, DirectExchange notifyExchange) {
        return BindingBuilder.bind(commentNotifyQueue).to(notifyExchange).with(ObjectType.COMMENT);
    }

    @Bean
    public Binding notifyExchangeBindIllustQueue(Queue illustNotifyQueue, DirectExchange notifyExchange) {
        return BindingBuilder.bind(illustNotifyQueue).to(notifyExchange).with(ObjectType.ILLUST);
    }

    @Bean
    public Binding notifyExchangeBindCollectionQueue(Queue collectionNotifyQueue, DirectExchange notifyExchange) {
        return BindingBuilder.bind(collectionNotifyQueue).to(notifyExchange).with(ObjectType.COLLECTION);
    }

}
