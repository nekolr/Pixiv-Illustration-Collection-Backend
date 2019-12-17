package dev.cheerfun.pixivic.biz.notify.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import dev.cheerfun.pixivic.biz.notify.service.NotifyEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
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
public class NotifyEventStreamListener implements StreamListener<String, MapRecord<String, String, String>> {
    private final ObjectMapper objectMapper;
    private final NotifyEventService notifyEventService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void onMessage(MapRecord<String, String, String> entries) {
        CompletableFuture.supplyAsync(() -> notifyEventService.dealNotifyEvent(objectMapper.convertValue(entries.getValue(), NotifyEvent.class)))
                .thenAccept(e-> System.out.println(stringRedisTemplate.opsForStream().acknowledge("n:e","email",entries.getId())));
      //  notifyEventService.dealNotifyEvent(objectMapper.convertValue(entries.getValue(), NotifyEvent.class));

    }
}
