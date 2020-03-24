package dev.cheerfun.pixivic.biz.notify.sender.impl;

import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.notify.sender.NotifySender;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 11:35 上午
 * @description MessageSender
 */
@Component("message")
public class MessageSender implements NotifySender {
    @Override
    public Boolean send(NotifyRemind notifyRemind) {
        //调用信息sdk
        return null;
    }
}
