package dev.cheerfun.pixivic.biz.notify.customer.impl;

import dev.cheerfun.pixivic.basic.event.constant.ObjectType;
import dev.cheerfun.pixivic.basic.event.domain.Event;
import dev.cheerfun.pixivic.biz.notify.constant.ActionType;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyChannel;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyStatus;
import dev.cheerfun.pixivic.biz.notify.customer.NotifyEventCustomer;
import dev.cheerfun.pixivic.biz.notify.po.Actor;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 2:33 下午
 * @description CollectionNotifyEventCustomer
 */
@Component(ObjectType.COLLECTION)
@RabbitListener(queues = ObjectType.COLLECTION)
public class CollectionNotifyEventCustomer extends NotifyEventCustomer {

    @Override
    @RabbitHandler()
    public void consume(Event event) {
        super.process(event);
    }

    @Override
    protected Object querySendTo(Event event) {
        //如果是发布 则找出所有关注发布者的用户
        //如果是收藏点赞则找出发布者
        switch (event.getAction()) {
            case ActionType.PUBLISH:
                break;
            case ActionType.LIKE:
            case ActionType.BOOKMARK:
                return queryUserIdByAppTypeAndAppId(ObjectType.COLLECTION, event.getObjectId());
            default:
                return 0;
        }
        return null;
    }

    @Override
    protected NotifyRemind generateRemind(Event event, Integer sendTo) {
        String message = queryTemplate(event.getObjectType(), event.getAction());
        String extend = null;
        String objectType = event.getObjectType();
        Integer objectId = event.getObjectId();
        String objectTitle = generateTitle(objectType, objectId);
        Integer type = remindTypeMap.get(event.getAction());
        List<Actor> actorList = new ArrayList<>();
        actorList.add(Actor.castFromUser(userCommonService.queryUser(event.getUserId())));
        switch (event.getAction()) {
            case ActionType.PUBLISH:
                break;
            case ActionType.LIKE:
                NotifyRemind notifyRemind = checkCanMerge(sendTo, type, event, objectType);
                if (notifyRemind != null) {
                    return notifyRemind;
                }
                break;
            case ActionType.BOOKMARK:
                break;
            default:
                return null;
        }
        return new NotifyRemind(null, type, actorList, actorList.size(), objectType, objectId, objectTitle, sendTo, message, null, event.getCreateDate(), NotifyStatus.UNREAD);
    }

    @Override
    protected Boolean send(NotifyRemind notifyRemind) {
        return notifySenderManager.send(notifyRemind, NotifyChannel.WEB);
    }
}
