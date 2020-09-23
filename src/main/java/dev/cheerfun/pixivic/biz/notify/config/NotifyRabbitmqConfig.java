package dev.cheerfun.pixivic.biz.notify.config;

import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import dev.cheerfun.pixivic.biz.notify.constant.RabbitmqConstant;
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

    @Bean(RabbitmqConstant.COMMENT_NOTIFY_QUEUE)
    public Queue commentNotifyQueue() {
        return new Queue(ObjectType.COMMENT);
    }

    @Bean(RabbitmqConstant.ILLUST_NOTIFY_QUEUE)
    public Queue illustNotifyQueue() {
        return new Queue(ObjectType.ILLUST);
    }

    @Bean(RabbitmqConstant.COLLECTION_NOTIFY_QUEUE)
    public Queue collectionNotifyQueue() {
        return new Queue(ObjectType.COLLECTION);
    }

    @Bean(RabbitmqConstant.DISCUSSION_NOTIFY_QUEUE)
    public Queue discussionNotifyQueue() {
        return new Queue(ObjectType.DISCUSSION);
    }

    @Bean(RabbitmqConstant.USER_NOTIFY_QUEUE)
    public Queue userNotifyQueue() {
        return new Queue(ObjectType.USER);
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

    @Bean
    public Binding notifyExchangeBindDiscussionQueue(Queue discussionNotifyQueue, DirectExchange notifyExchange) {
        return BindingBuilder.bind(discussionNotifyQueue).to(notifyExchange).with(ObjectType.DISCUSSION);
    }

    @Bean
    public Binding notifyExchangeBindUserQueue(Queue userNotifyQueue, DirectExchange notifyExchange) {
        return BindingBuilder.bind(userNotifyQueue).to(notifyExchange).with(ObjectType.USER);
    }

}
