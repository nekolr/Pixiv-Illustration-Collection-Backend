package dev.cheerfun.pixivic.biz.notify.customer.impl;

import dev.cheerfun.pixivic.biz.notify.constant.ActionType;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyChannel;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyObjectType;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyStatus;
import dev.cheerfun.pixivic.biz.notify.customer.NotifyEventCustomer;
import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
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
@Component(NotifyObjectType.ILLUST)
@RabbitListener(queues = NotifyObjectType.ILLUST)
public class IllustNotifyEventCustomer extends NotifyEventCustomer {

    @Override
    @RabbitHandler()
    public void consume(NotifyEvent notifyEvent) {
        process(notifyEvent);
    }

    @Override
    protected List<Integer> querySendTo(NotifyEvent notifyEvent) {
        //找出画师关注者
        Set<String> members = stringRedisTemplate.opsForSet().members(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + notifyEvent.getUserId());
        assert members != null;
        return members.stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    @Override
    protected NotifyRemind generateRemind(NotifyEvent notifyEvent, Integer sendTo) {
        return new NotifyRemind(null, notifyEvent.getUserId(), notifyEvent.getUserName(), ActionType.RELEASED, notifyEvent.getObjectId(), notifyEvent.getObjectType(), sendTo, queryTemplate(notifyEvent), LocalDateTime.now(), NotifyStatus.UNREAD, null);
    }

    @Override
    protected Boolean send(NotifyRemind notifyRemind) {
        return notifySenderManager.send(notifyRemind, NotifyChannel.WEB);
    }
}
