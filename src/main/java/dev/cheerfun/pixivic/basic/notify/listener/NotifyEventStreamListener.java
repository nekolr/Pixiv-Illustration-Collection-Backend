package dev.cheerfun.pixivic.basic.notify.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.basic.notify.po.NotifyEvent;
import dev.cheerfun.pixivic.basic.notify.service.NotifyEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-16 下午9:56
 * @description NotifyEventStreamListener
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotifyEventStreamListener implements StreamListener<String, ObjectRecord<String, Object>> {
    private final ObjectMapper objectMapper;
    private final NotifyEventService notifyEventService;
    private final StringRedisTemplate stringRedisTemplate;
    private final static String NOTIFYEVENTSTREAMKEY = "n:e";
    private final static String NOTIFYEVENTSTREAMEMAILGROUP = "common";

    @Override
    public void onMessage(ObjectRecord<String, Object> entries) {
        //处理成功后ack
        CompletableFuture.supplyAsync(() -> notifyEventService.dealNotifyEvent(objectMapper.convertValue(entries.getValue(), NotifyEvent.class)))
                .thenAccept(e -> {
                    if (e) {
                        stringRedisTemplate.opsForStream().acknowledge(NOTIFYEVENTSTREAMKEY, NOTIFYEVENTSTREAMEMAILGROUP, entries.getId());
                    }
                });
    }
}
