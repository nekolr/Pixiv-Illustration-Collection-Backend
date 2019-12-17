package dev.cheerfun.pixivic.biz.notify.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-16 下午9:39
 * @description NotifyStreamConfig
 */
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotifyStreamConfig {
    private final StreamListener streamListener;
    private final RedisConnectionFactory redisConnectionFactory;
    private final StringRedisTemplate stringRedisTemplate;
    private final static String NOTIFYEVENTSTREAMKEY = "n:e";

    @PostConstruct
    public void init() {
        initNotifySetting();
        listenerNotifyEvent();
    }

    private void listenerNotifyEvent() {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> containerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder().pollTimeout(Duration.ofMillis(100)).build();
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container = StreamMessageListenerContainer.create(redisConnectionFactory,
                containerOptions);
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container2 = StreamMessageListenerContainer.create(redisConnectionFactory,
                containerOptions);
        container.start();
        container.receive(StreamOffset.fromStart(NOTIFYEVENTSTREAMKEY), streamListener);
    }

    private void initNotifySetting() {
        //从配置文件或数据库读取通知设定
    }

}
