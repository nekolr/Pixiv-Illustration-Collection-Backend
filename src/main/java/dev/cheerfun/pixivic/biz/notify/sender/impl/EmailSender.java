package dev.cheerfun.pixivic.biz.notify.sender.impl;

import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.notify.sender.NotifySender;
import dev.cheerfun.pixivic.common.util.email.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 11:34 上午
 * @description EmailSender
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailSender implements NotifySender {
    private final EmailUtil emailUtil;

    @Override
    public Boolean send(NotifyRemind notifyRemind) {
        //根据id查出邮箱
        //发送

        return null;
    }
}
