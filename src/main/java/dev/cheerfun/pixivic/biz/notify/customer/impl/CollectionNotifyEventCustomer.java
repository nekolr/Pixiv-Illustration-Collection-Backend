package dev.cheerfun.pixivic.biz.notify.customer.impl;

import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import dev.cheerfun.pixivic.biz.event.domain.Event;
import dev.cheerfun.pixivic.biz.notify.customer.NotifyEventCustomer;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 2:33 下午
 * @description CollectionNotifyEventCustomer
 */
@Component(ObjectType.COLLECTION)
@RabbitListener(queues = ObjectType.COLLECTION)
public class CollectionNotifyEventCustomer extends NotifyEventCustomer {

    @Override
    @RabbitHandler()
    public void consume(Event event) {
        super.process(event);
    }

    @Override
    protected Object querySendTo(Event event) {
        //如果是发布 则找出所有关注发布者的用户
        //如果是收藏评论则找出发布者
        return null;
    }

    @Override
    protected NotifyRemind generateRemind(Event event, Integer sendTo) {
        return null;
    }

    @Override
    protected Boolean send(NotifyRemind notifyRemind) {
        return null;
    }
}
