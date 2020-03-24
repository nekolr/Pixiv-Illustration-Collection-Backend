package dev.cheerfun.pixivic.biz.notify.sender;

import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 11:43 上午
 * @description NotifySenderManager
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotifySenderManager {
    private final Map<String, NotifySender> notifySenderMap;

    public Boolean send(NotifyRemind notifyRemind, String notifySender) {
        return notifySenderMap.get(notifySender).send(notifyRemind);
    }

    public Boolean send(NotifyRemind notifyRemind, String... notifySenders) {
        for (String notifySender : notifySenders) {
            send(notifyRemind, notifySender);
        }
        return true;
    }

}
