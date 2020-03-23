package dev.cheerfun.pixivic.biz.notify.sender;

import dev.cheerfun.pixivic.biz.notify.constant.NotifyObjectType;
import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 3:00 下午
 * @description NotifyEventSender
 */
@Component
public class NotifyEventSender {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send(NotifyEvent notifyEvent) {
        this.rabbitTemplate.convertAndSend(notifyEvent.getObjectType(), notifyEvent);
    }

    //@Scheduled(cron = "0/10 * * * * ?")
    public void send() {
        this.rabbitTemplate.convertAndSend(NotifyObjectType.COMMENT, new NotifyEvent());
    }
}
