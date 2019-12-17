package dev.cheerfun.pixivic.biz.notify.service;

import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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

    public boolean dealNotifyEvent(NotifyEvent notifyEvent) {
        System.out.println(notifyEvent);

        return true;
    }

}
