package dev.cheerfun.pixivic.biz.notify.customer;

import dev.cheerfun.pixivic.biz.notify.mapper.NotifyMapper;
import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.notify.po.NotifySettingConfig;
import dev.cheerfun.pixivic.biz.notify.sender.NotifySenderManager;
import dev.cheerfun.pixivic.biz.notify.util.UserSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public abstract class NotifyEventCustomer {
    @Autowired
    protected NotifyMapper notifyMapper;
    @Autowired
    protected NotifySenderManager notifySenderManager;
    @Autowired
    protected UserSettingUtil userSettingUtil;
    protected Map<String, NotifySettingConfig> notifySettingMap;
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    void init() {
        notifySettingMap = notifyMapper.queryNotifySettingConfig().stream().collect(Collectors.toMap(e -> e.getObjectType() + e.getObjectRelationship() + e.getAction(), e -> e));
    }

    protected void process(NotifyEvent notifyEvent) {
        //查找出所有收件方
        Object sendTo = querySendTo(notifyEvent);
        //生成remind
        List<NotifyRemind> notifyReminds = new ArrayList<>();
        if (sendTo instanceof List) {
            for (Integer userId : (List<Integer>) sendTo) {
                //校验是否接收消息
                if (userSettingUtil.checkUserSetting(userId, notifyEvent.getObjectType())) {
                    notifyReminds.add(generateRemind(notifyEvent, userId));
                }
            }
            //发送
            for (NotifyRemind notifyRemind : notifyReminds) {
                send(notifyRemind);
            }
        } else {
            send(generateRemind(notifyEvent, (int) sendTo));
        }

    }

    protected abstract void consume(NotifyEvent notifyEvent);

    protected abstract Object querySendTo(NotifyEvent notifyEvent);

    protected abstract NotifyRemind generateRemind(NotifyEvent notifyEvent, Integer sendTo);

    protected abstract Boolean send(NotifyRemind notifyRemind);

    protected String queryTemplate(NotifyEvent notifyEvent) {
        NotifySettingConfig notifySettingConfig = notifySettingMap.get(notifyEvent.getObjectType() + ":" + notifyEvent.getObjectId());
        return notifySettingConfig.getMessageTemplate();
    }

}
