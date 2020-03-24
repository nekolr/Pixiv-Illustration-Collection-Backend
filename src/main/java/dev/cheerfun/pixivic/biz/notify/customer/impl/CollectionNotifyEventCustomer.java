package dev.cheerfun.pixivic.biz.notify.customer.impl;

import dev.cheerfun.pixivic.biz.notify.constant.NotifyObjectType;
import dev.cheerfun.pixivic.biz.notify.customer.NotifyEventCustomer;
import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 2:33 下午
 * @description CollectionNotifyEventCustomer
 */
@Component(NotifyObjectType.COLLECTION)
@RabbitListener(queues = NotifyObjectType.COLLECTION)
public class CollectionNotifyEventCustomer extends NotifyEventCustomer {

    @Override
    @RabbitHandler()
    public void consume(NotifyEvent notifyEvent) {
        super.process(notifyEvent);
    }

    @Override
    protected Object querySendTo(NotifyEvent notifyEvent) {
        //如果是发布 则找出所有关注发布者的用户
        //如果是收藏评论则找出发布者
        return null;
    }

    @Override
    protected NotifyRemind generateRemind(NotifyEvent notifyEvent, Integer sendTo) {
        return null;
    }

    @Override
    protected Boolean send(NotifyRemind notifyRemind) {
        return null;
    }
}
