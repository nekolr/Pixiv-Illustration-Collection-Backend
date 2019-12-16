package dev.cheerfun.pixivic.biz.notify.service;

import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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
public class NotifyService {
    private final StringRedisTemplate stringRedisTemplate;
    private final static String NOTIFYEVENTSTREAMKEY = "n:e";

    @Scheduled(cron = "0/1 * * * * ? ")
    public void pushNotifyEvent() {
        ObjectRecord<String, NotifyEvent> objectRecord = StreamRecords.newRecord().ofObject(new NotifyEvent(1,"a",1,"a", LocalDateTime.now())).withStreamKey(NOTIFYEVENTSTREAMKEY);
        stringRedisTemplate.opsForStream().add(objectRecord);
    }

    public void pullNotifyEvent() {

    }

}
