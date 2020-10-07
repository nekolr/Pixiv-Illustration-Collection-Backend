package dev.cheerfun.pixivic.biz.notify.customer.impl;

import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import dev.cheerfun.pixivic.biz.event.domain.Event;
import dev.cheerfun.pixivic.biz.notify.constant.ActionType;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyChannel;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyStatus;
import dev.cheerfun.pixivic.biz.notify.customer.NotifyEventCustomer;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 2:32 下午
 * @description IllustNotifyEventCustomer
 */
@Component(ObjectType.ILLUST)
@RabbitListener(queues = ObjectType.ILLUST)
public class IllustNotifyEventCustomer extends NotifyEventCustomer {

    @Override
    @RabbitHandler()
    public void consume(Event event) {
        process(event);
    }

    @Override
    protected List<Integer> querySendTo(Event event) {
        //找出画师关注者
        Set<String> members = stringRedisTemplate.opsForSet().members(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + event.getUserId());
        assert members != null;
        return members.stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    @Override
    protected NotifyRemind generateRemind(Event event, Integer sendTo) {
       // return new NotifyRemind(null, event.getUserId(), event.getUserName(), ActionType.RELEASE, event.getObjectId(), event.getObjectType(), sendTo, queryTemplate(event), LocalDateTime.now(), NotifyStatus.UNREAD, null);
        return null;
    }

    @Override
    protected Boolean send(NotifyRemind notifyRemind) {
        return notifySenderManager.send(notifyRemind, NotifyChannel.WEB);
    }
}
