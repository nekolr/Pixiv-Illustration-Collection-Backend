package dev.cheerfun.pixivic.basic.event.publisher;

import dev.cheerfun.pixivic.basic.event.constant.RabbitmqConstant;
import dev.cheerfun.pixivic.basic.event.domain.Event;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EventPublisher {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void publish(Event event) {
        //发布事件
        try {
            this.rabbitTemplate.convertAndSend(RabbitmqConstant.FANOUT_EXCHANGE, event.getObjectType(), event);
        } catch (Exception e) {
            log.error("发布事件失败");
            e.printStackTrace();
        }
    }
}
