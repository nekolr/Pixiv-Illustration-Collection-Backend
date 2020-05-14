package dev.cheerfun.pixivic.biz.notify.customer;

import dev.cheerfun.pixivic.biz.event.domain.Event;
import dev.cheerfun.pixivic.biz.notify.mapper.NotifyMapper;
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
        notifySettingMap = notifyMapper.queryNotifySettingConfig().stream().collect(Collectors.toMap(e -> e.getObjectType() + ":" + e.getAction(), e -> e));
    }

    protected void process(Event event) {
        //查找出所有收件方
        try {
            Object sendTo = querySendTo(event);
            //生成remind
            List<NotifyRemind> notifyReminds = new ArrayList<>();
            if (sendTo instanceof List) {
                for (Integer userId : (List<Integer>) sendTo) {
                    //校验是否接收消息
                    if (userSettingUtil.checkUserSetting(userId, event.getObjectType())) {
                        notifyReminds.add(generateRemind(event, userId));
                    }
                }
                //发送
                for (NotifyRemind notifyRemind : notifyReminds) {
                    send(notifyRemind);
                }
            } else {
                send(generateRemind(event, (int) sendTo));
            }
        } catch (Exception e) {
            System.err.println("消息消费出错，详情:");
            e.printStackTrace();
        }

    }

    protected abstract void consume(Event event);

    protected abstract Object querySendTo(Event event);

    protected abstract NotifyRemind generateRemind(Event event, Integer sendTo);

    protected abstract Boolean send(NotifyRemind notifyRemind);

    protected String queryTemplate(Event event) {
        NotifySettingConfig notifySettingConfig = notifySettingMap.get(event.getObjectType() + ":" + event.getAction());
        return notifySettingConfig.getMessageTemplate();
    }

}
