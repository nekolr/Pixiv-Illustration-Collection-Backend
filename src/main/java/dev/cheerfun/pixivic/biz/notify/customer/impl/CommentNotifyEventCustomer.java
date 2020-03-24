package dev.cheerfun.pixivic.biz.notify.customer.impl;

import dev.cheerfun.pixivic.biz.notify.constant.NotifyChannel;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyObjectType;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyStatus;
import dev.cheerfun.pixivic.biz.notify.customer.NotifyEventCustomer;
import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.notify.po.NotifySettingConfig;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 11:23 上午
 * @description CommentNotifyEventCustomer
 */
@Component(NotifyObjectType.COMMENT)
@RabbitListener(queues = NotifyObjectType.COMMENT)
public class CommentNotifyEventCustomer extends NotifyEventCustomer {

    @Override
    @RabbitHandler()
    public void consume(NotifyEvent notifyEvent) {
        super.process(notifyEvent);
    }

    @Override
    protected Integer querySendTo(NotifyEvent notifyEvent) {
        Comment comment = queryCommentById(notifyEvent);
        return comment.getReplyTo();
    }

    @Cacheable(value = "comments", key = "#notifyEvent.objectId")
    public Comment queryCommentById(NotifyEvent notifyEvent) {
        return notifyMapper.queryCommentById(notifyEvent.getObjectId());
    }

    @Override
    protected NotifyRemind generateRemind(NotifyEvent notifyEvent, Integer sendTo) {
        Comment comment = queryCommentById(notifyEvent);
        //查出对应comment对象的评论主体并生成提醒
        return new NotifyRemind(null, comment.getReplyFrom(), comment.getReplyFromName(), notifyEvent.getAction(), comment.getAppId(), comment.getAppType(), comment.getReplyTo(), queryTemplate(notifyEvent), LocalDateTime.now(), NotifyStatus.UNREAD, null);
    }

    @Override
    protected Boolean send(NotifyRemind notifyRemind) {
        return notifySenderManager.send(notifyRemind, NotifyChannel.WEB);
    }

}
