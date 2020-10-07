package dev.cheerfun.pixivic.biz.event.publisher;

import dev.cheerfun.pixivic.biz.event.constant.ActionType;
import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import dev.cheerfun.pixivic.biz.event.constant.RabbitmqConstant;
import dev.cheerfun.pixivic.biz.event.domain.Event;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
        //发布事件
        this.rabbitTemplate.convertAndSend(RabbitmqConstant.FANOUT_EXCHANGE, event.getObjectType(), event);
    }

    //@Scheduled(cron = "0/10 * * * * ?")
    public void publish() {
        //this.rabbitTemplate.convertAndSend(RabbitmqConstant.FANOUT_EXCHANGE, ObjectType.COMMENT, new Event(1, "test", ActionType.RELEASE, ObjectType.COLLECTION, 1, LocalDateTime.now()));
    }
}
