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
 * @date 2020/3/18 2:32 下午
 * @description IllustNotifyEventCustomer
 */
@Component(NotifyObjectType.ILLUST)
@RabbitListener(queues = NotifyObjectType.ILLUST)
public class IllustNotifyEventCustomer extends NotifyEventCustomer {

    @Override
    @RabbitHandler()
    public void process(NotifyEvent notifyEvent) {

    }
}
