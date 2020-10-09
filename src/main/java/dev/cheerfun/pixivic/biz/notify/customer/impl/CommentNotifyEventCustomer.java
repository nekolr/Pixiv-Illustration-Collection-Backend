package dev.cheerfun.pixivic.biz.notify.customer.impl;

import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import dev.cheerfun.pixivic.biz.event.domain.Event;
import dev.cheerfun.pixivic.biz.notify.constant.ActionType;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyChannel;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyStatus;
import dev.cheerfun.pixivic.biz.notify.customer.NotifyEventCustomer;
import dev.cheerfun.pixivic.biz.notify.po.Actor;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 11:23 上午
 * @description CommentNotifyEventCustomer
 */
@Component(ObjectType.COMMENT)
@RabbitListener(queues = ObjectType.COMMENT)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommentNotifyEventCustomer extends NotifyEventCustomer {

    @Override
    @RabbitHandler()
    public void consume(Event event) {
        super.process(event);
    }

    @Override
    protected Integer querySendTo(Event event) {
        Comment comment = commentService.queryCommentById(event.getObjectId());
        //根据不同动作进行处理
        switch (event.getAction()) {
            case ActionType.LIKE:
                return queryUserIdByAppTypeAndAppId(ObjectType.COMMENT, event.getObjectId());
            case ActionType.PUBLISH:
                if (comment.getParentId().compareTo(0) == 0) {
                    //如果是顶级评论则找到appid的所有者
                    return queryUserIdByAppTypeAndAppId(comment.getAppType(), comment.getAppId());
                } else {
                    //如果非顶级则找回复者
                    return comment.getReplyTo();
                }
            default:
                return 0;
        }
    }

    @Override
    protected NotifyRemind generateRemind(Event event, Integer sendTo) {
        Comment comment = commentService.queryCommentById(event.getObjectId());
        String username = userCommonService.queryUser(event.getUserId()).getUsername();
        //查出对应comment对象的评论主体并生成提醒
        String message = null;
        String objectType = null;
        String objectTitle = null;
        Integer objectId = null;
        Integer type = null;
        List<Actor> actorList = new ArrayList<>();
        //根据不同动作进行处理
        switch (event.getAction()) {
            case ActionType.LIKE:
                type = remindTypeMap.get(ActionType.LIKE);
                objectType = ObjectType.COMMENT;
                objectId = event.getObjectId();
                message = queryTemplate(comment.getAppType(), ActionType.LIKE);
                //需要进行合并，查找之前的点赞记录 根据create判断是否有需要合并的 合并的时候从头部插入 返回的时候仅仅返回前几个
                List<NotifyRemind> notifyReminds = notifyRemindService.queryRecentlyRemind(sendTo, type, LocalDateTime.now().plusHours(-24));
                if (notifyReminds.size() > 0) {
                    Optional<NotifyRemind> oldRemind = notifyReminds.stream().filter(e -> event.getObjectType().equals(ObjectType.COMMENT) && event.getObjectId().compareTo(e.getObjectId()) == 0).findFirst();
                    if (oldRemind.isPresent()) {
                        NotifyRemind notifyRemind = oldRemind.get();
                        //不能单纯add 需要考虑重复问题
                        notifyRemind.getActors().add(Actor.castFromUser(userCommonService.queryUser(sendTo)));
                        notifyRemind.setCreateDate(event.getCreateDate());
                        notifyRemind.setActorCount(notifyRemind.getActors().size());
                        return notifyRemind;
                    }
                }
                break;
            case ActionType.PUBLISH:
                if (comment.getParentId().compareTo(0) == 0) {
                    //如果是顶级评论则找到apptype
                    type = remindTypeMap.get(ActionType.COMMENT);
                    objectType = comment.getAppType();
                    objectId = comment.getAppId();
                    message = queryTemplate(comment.getAppType(), ActionType.COMMENT);
                } else {
                    //如果非顶级
                    type = remindTypeMap.get(ActionType.REPLY);
                    objectType = event.getObjectType();
                    objectId = event.getObjectId();
                    message = queryTemplate(ObjectType.COMMENT, ActionType.REPLY);
                    type = remindTypeMap.get(ActionType.REPLY);
                }
                actorList.add(Actor.castFromUser(userCommonService.queryUser(event.getUserId())));
                break;
            default:
                break;
        }
        objectTitle = generateTitle(objectType, objectId);
        return new NotifyRemind(null, type, actorList, actorList.size(), objectType, objectId, objectTitle, sendTo, message, event.getCreateDate(), NotifyStatus.UNREAD, null);
    }

    @Override
    protected Boolean send(NotifyRemind notifyRemind) {
        return notifySenderManager.send(notifyRemind, NotifyChannel.WEB);
    }

}
