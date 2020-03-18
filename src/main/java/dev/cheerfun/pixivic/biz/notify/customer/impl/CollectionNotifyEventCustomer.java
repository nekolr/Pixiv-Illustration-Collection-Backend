package dev.cheerfun.pixivic.biz.notify.customer.impl;

import dev.cheerfun.pixivic.biz.notify.constant.NotifyObjectType;
import dev.cheerfun.pixivic.biz.notify.customer.NotifyEventCustomer;
import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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
    public void process(NotifyEvent notifyEvent) {
        System.out.println("Receiver  : " + notifyEvent);
    }
}
