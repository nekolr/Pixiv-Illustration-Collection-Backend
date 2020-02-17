package dev.cheerfun.pixivic.basic.notify.service;

import dev.cheerfun.pixivic.basic.notify.po.NotifyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    private final static String NOTIFYEVENTSTREAMKEY = "n:e";

    public void pushNotifyEvent(NotifyEvent notifyEvent) {
        ObjectRecord<String, NotifyEvent> objectRecord = StreamRecords.newRecord()
                .ofObject(notifyEvent).withStreamKey(NOTIFYEVENTSTREAMKEY);
        stringRedisTemplate.opsForStream().add(objectRecord);
    }

    //@Scheduled(cron = "0/1 * * * * ? ")
    public void pushNotifyEvent() {
        ObjectRecord<String, NotifyEvent> objectRecord = StreamRecords.newRecord()
                .ofObject(new NotifyEvent(1, "a", 1, "a", LocalDateTime.now())).withStreamKey(NOTIFYEVENTSTREAMKEY);
        stringRedisTemplate.opsForStream().add(objectRecord);
    }

    public boolean dealNotifyEvent(NotifyEvent notifyEvent) {
        //notifyEvent进来，先取用户设定（是否不发送），若发送则取对应事件的notify_setting_config

        //取出notify_setting_config形成对应的notifyRemind存入数据库或进行其他操作（根据相应的channel来判断）
        System.out.println(notifyEvent);

        return true;
    }

}
