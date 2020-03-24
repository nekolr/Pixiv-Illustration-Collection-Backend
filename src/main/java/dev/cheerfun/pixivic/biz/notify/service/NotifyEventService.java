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






}
