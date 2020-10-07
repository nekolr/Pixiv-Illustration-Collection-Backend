package dev.cheerfun.pixivic.biz.notify.customer.impl;

import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import dev.cheerfun.pixivic.biz.event.domain.Event;
import dev.cheerfun.pixivic.biz.notify.constant.ActionType;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyChannel;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyStatus;
import dev.cheerfun.pixivic.biz.notify.customer.NotifyEventCustomer;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import dev.cheerfun.pixivic.biz.web.comment.constant.CommentAppType;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        switch (event.getAction()) {
            case ActionType.LIKE:
                return null;
            case ActionType.PUBLISH:
                if (comment.getParentId().compareTo(0) == 0) {
                    //如果是顶级评论则找到appid的所有者
                    switch (comment.getAppType()) {
                        case CommentAppType.COLLECTION: {
                            Collection collection = collectionService.queryCollectionByIdFromDb(comment.getAppId());
                            return collection.getUserId();
                        }
                        default:
                            return 0;
                    }
                } else {
                    //如果非顶级则找回复者
                    return comment.getReplyTo();
                }
            default:
                return null;
        }
    }

    @Override
    protected NotifyRemind generateRemind(Event event, Integer sendTo) {
        Comment comment = commentService.queryCommentById(event.getObjectId());
        String username = userCommonService.queryUser(event.getUserId()).getUsername();
        //查出对应comment对象的评论主体并生成提醒
        String message;
        String objectType;
        Integer objectId;
        String action;
        if (comment.getParentId().compareTo(0) == 0) {
            //如果是顶级评论则找到apptype
            message = queryTemplate(comment.getAppType(), ActionType.COMMENT);
            objectType = comment.getAppType();
            objectId = comment.getAppId();
            action = ActionType.COMMENT;
        } else {
            //如果非顶级
            objectType = event.getObjectType();
            objectId = event.getObjectId();
            message = queryTemplate(ObjectType.COMMENT, ActionType.REPLY);
            action = ActionType.REPLY;
        }
        return new NotifyRemind(null, event.getUserId(), username, event.getAction(), objectId, objectType, sendTo, message, event.getCreateDate(), NotifyStatus.UNREAD, null);
    }

    @Override
    protected Boolean send(NotifyRemind notifyRemind) {
        return notifySenderManager.send(notifyRemind, NotifyChannel.WEB);
    }

}
