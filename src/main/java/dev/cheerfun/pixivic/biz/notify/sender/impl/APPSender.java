package dev.cheerfun.pixivic.biz.notify.sender.impl;

import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.notify.sender.NotifySender;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 11:35 上午
 * @description APPSender
 */
@Component("app")
public class APPSender implements NotifySender {
    @Override
    public Boolean send(NotifyRemind notifyRemind) {
        //推送
        return null;
    }
}
