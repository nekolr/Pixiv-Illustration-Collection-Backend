package dev.cheerfun.pixivic.biz.notify.service;

import dev.cheerfun.pixivic.biz.notify.constant.NotifyObjectType;
import dev.cheerfun.pixivic.biz.notify.constant.NotifyStatus;
import dev.cheerfun.pixivic.biz.notify.mapper.NotifyMapper;
import dev.cheerfun.pixivic.biz.notify.po.NotifyBanSetting;
import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.notify.po.NotifySettingConfig;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-14 下午9:47
 * @description NotifyService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotifyEventService {
    private final StringRedisTemplate stringRedisTemplate;
    private final NotifyMapper notifyMapper;
    private final static String NOTIFYEVENTSTREAMKEY = "n:e";
    private Map<String, List<NotifySettingConfig>> notifySettingMap;

    @PostConstruct
    public void init() {
        //初始化的时候初始化notify_setting_config
        notifySettingMap = notifyMapper.queryNotifySettingConfig().stream().collect(Collectors.groupingBy(e -> e.getObjectType() + ":" + e.getAction()));

    }

    public void pushNotifyEvent(NotifyEvent notifyEvent) {
        ObjectRecord<String, NotifyEvent> objectRecord = StreamRecords.newRecord()
                .ofObject(notifyEvent).withStreamKey(NOTIFYEVENTSTREAMKEY);
        stringRedisTemplate.opsForStream().add(objectRecord);
    }

    public void pushNotifyEvent() {
        ObjectRecord<String, NotifyEvent> objectRecord = StreamRecords.newRecord()
                .ofObject(new NotifyEvent(1, "a", 1, "a", LocalDateTime.now())).withStreamKey(NOTIFYEVENTSTREAMKEY);
        stringRedisTemplate.opsForStream().add(objectRecord);
    }

    public boolean dealNotifyEvent(NotifyEvent notifyEvent) {
        String objectType = notifyEvent.getObjectType();
        if (NotifyObjectType.COMMENT.equals(objectType)) {
            dealCommentEvent(notifyEvent);
        } else if (NotifyObjectType.ILLUST.equals(objectType)) {
            dealIllustEvent(notifyEvent);
        }
        //notifyEvent进来，先取用户设定（是否不发送），若发送则取对应事件的notify_setting_config

        //取出notify_setting_config形成对应的notifyRemind存入数据库或进行其他操作（根据相应的channel来判断）
        System.out.println(notifyEvent);
        return true;
    }

    @Cacheable("userNotifySetting")
    public Boolean checkUserSetting(Integer ownerId, String objectType) {
        NotifyBanSetting notifyBanSetting = notifyMapper.queryUserBanSetting(ownerId);
        return notifyBanSetting == null || !notifyBanSetting.getBanNotifyActionType().contains(objectType);
    }

    private void dealCommentEvent(NotifyEvent notifyEvent) {
        //找到事件对象所有者
        Comment comment = notifyMapper.queryCommentById(notifyEvent.getObjectId());
        Integer ownerId = comment.getReplyTo();
        //校验是否接受通知
        if (checkUserSetting(ownerId, NotifyObjectType.COMMENT)) {
            //拼接通知入库
            String action = notifyEvent.getAction();
            String objectType = notifyEvent.getObjectType();
            NotifySettingConfig notifySettingConfig = notifySettingMap.get(objectType + ":" + action).get(0);
            //查出对应comment对象的评论主体并生成提醒
            new NotifyRemind(null, comment.getReplyFrom(), comment.getReplyFromName(), action, comment.getAppId(), comment.getAppType(), comment.getReplyTo(), notifySettingConfig.getMessageTemplate(), LocalDateTime.now(), NotifyStatus.UNREAD, null);

        }

    }

    private void dealIllustEvent(NotifyEvent notifyEvent) {

    }

}
