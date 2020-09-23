package dev.cheerfun.pixivic.biz.notify.customer.impl;

import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import dev.cheerfun.pixivic.biz.event.domain.Event;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyChannel;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyStatus;
import dev.cheerfun.pixivic.biz.notify.customer.NotifyEventCustomer;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
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
@Component(ObjectType.COMMENT)
@RabbitListener(queues = ObjectType.COMMENT)
public class CommentNotifyEventCustomer extends NotifyEventCustomer {

    @Override
    @RabbitHandler()
    public void consume(Event event) {
        System.out.println(event);
        super.process(event);
    }

    @Override
    protected Integer querySendTo(Event event) {
        Comment comment = queryCommentById(event);
        return comment.getReplyTo();
    }

    @Cacheable(value = "comments", key = "#event.objectId")
    public Comment queryCommentById(Event event) {
        return notifyMapper.queryCommentById(event.getObjectId());
    }

    @Override
    protected NotifyRemind generateRemind(Event event, Integer sendTo) {
        Comment comment = queryCommentById(event);
        //查出对应comment对象的评论主体并生成提醒
        return new NotifyRemind(null, event.getUserId(), event.getUserName(), event.getAction(), event.getObjectId(), event.getObjectType(), comment.getReplyFrom(), queryTemplate(event), LocalDateTime.now(), NotifyStatus.UNREAD, null);
    }

    @Override
    protected Boolean send(NotifyRemind notifyRemind) {
        return notifySenderManager.send(notifyRemind, NotifyChannel.WEB);
    }

}
