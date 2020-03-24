package dev.cheerfun.pixivic.biz.notify.publisher;

import dev.cheerfun.pixivic.biz.notify.constant.NotifyObjectType;
import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 3:00 下午
 * @description NotifyEventSender
 */
@Component
public class NotifyEventPublisher {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void publish(NotifyEvent notifyEvent) {
        //更具事件主体类型分发不同的queue
        this.rabbitTemplate.convertAndSend(notifyEvent.getObjectType(), notifyEvent);
    }

    //@Scheduled(cron = "0/10 * * * * ?")
    public void publish() {
        this.rabbitTemplate.convertAndSend(NotifyObjectType.COMMENT, new NotifyEvent());
    }
}
