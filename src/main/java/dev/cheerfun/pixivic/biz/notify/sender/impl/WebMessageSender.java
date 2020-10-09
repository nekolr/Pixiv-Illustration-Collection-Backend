package dev.cheerfun.pixivic.biz.notify.sender.impl;

import dev.cheerfun.pixivic.biz.notify.mapper.NotifyMapper;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.notify.sender.NotifySender;
import dev.cheerfun.pixivic.biz.notify.service.NotifyRemindService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 11:35 上午
 * @description WebMessageSender
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebMessageSender implements NotifySender {
    private final NotifyRemindService notifyRemindService;

    @Override
    @Transactional
    public Boolean send(NotifyRemind notifyRemind) {
        //直接持久化到数据库
        System.out.println(notifyRemind);
        if (notifyRemind.getId() != null) {
            notifyRemindService.updateRemindActorAndCreateDate(notifyRemind);
        } else {
            notifyRemindService.insertNotifyRemind(notifyRemind);
        }
        //汇总记录累加
        //首先查看记录是否存在
        notifyRemindService.updateRemindSummary(notifyRemind);
        return true;
    }
}
