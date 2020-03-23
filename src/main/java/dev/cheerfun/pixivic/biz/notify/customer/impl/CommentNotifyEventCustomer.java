package dev.cheerfun.pixivic.biz.notify.customer.impl;

import dev.cheerfun.pixivic.biz.notify.constant.NotifyObjectType;
import dev.cheerfun.pixivic.biz.notify.customer.NotifyEventCustomer;
import dev.cheerfun.pixivic.biz.notify.mapper.NotifyMapper;
import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 11:23 上午
 * @description CommentNotifyEventCustomer
 */
@Component(NotifyObjectType.COMMENT)
@RabbitListener(queues = NotifyObjectType.COMMENT)
public class CommentNotifyEventCustomer extends NotifyEventCustomer {

    @Override
    @RabbitHandler()
    public void process(NotifyEvent notifyEvent) {
        System.out.println(notifyEvent);
    }
}
