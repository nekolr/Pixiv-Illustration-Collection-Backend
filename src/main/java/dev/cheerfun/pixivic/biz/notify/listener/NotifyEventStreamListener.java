package dev.cheerfun.pixivic.biz.notify.listener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-16 下午9:56
 * @description NotifyEventStreamListener
 */
@Component
public class NotifyEventStreamListener implements StreamListener<String, MapRecord<String, String, String>> {
    @Override
    public void onMessage(MapRecord<String, String, String> entries) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        System.out.println("MessageId: " + entries.getId());
        System.out.println("Stream: " + entries.getStream());
        System.out.println("Body: " +objectMapper.convertValue(entries.getValue(),NotifyEvent.class));
        System.out.println("Body: " +entries.getValue());
    }
}
