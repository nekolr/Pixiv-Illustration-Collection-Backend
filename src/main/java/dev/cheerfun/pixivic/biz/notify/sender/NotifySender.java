package dev.cheerfun.pixivic.biz.notify.sender;

import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;

public interface NotifySender {
    Boolean send(NotifyRemind notifyRemind);
}
