package dev.cheerfun.pixivic.biz.event.publisher;

import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import dev.cheerfun.pixivic.biz.event.domain.Event;
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
public class EventPublisher {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void publish(Event event) {
        //更具事件主体类型分发不同的queue
        this.rabbitTemplate.convertAndSend(event.getObjectType(), event.getObjectType(), event);
    }

    //@Scheduled(cron = "0/10 * * * * ?")
    public void publish() {
        this.rabbitTemplate.convertAndSend(ObjectType.COMMENT, new Event());
    }
}
