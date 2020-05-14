package dev.cheerfun.pixivic.biz.notify.sender.impl;

import dev.cheerfun.pixivic.biz.notify.mapper.NotifyMapper;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.notify.sender.NotifySender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 11:35 上午
 * @description WebMessageSender
 */
@Component("web")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebMessageSender implements NotifySender {
    private final NotifyMapper notifyMapper;

    @Override
    public Boolean send(NotifyRemind notifyRemind) {
        //直接持久化到数据库
        System.out.println(notifyRemind);
        notifyMapper.insertNotifyRemind(notifyRemind);
        return null;
    }
}
