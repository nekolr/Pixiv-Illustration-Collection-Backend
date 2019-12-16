package dev.cheerfun.pixivic.biz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-16 下午9:39
 * @description NotifyStreamConfig
 */
@Configuration
public class NotifyStreamConfig {
    @PostConstruct
    public void init() {

    }

    @Bean
    public Subscription listener(StreamListener streamListener, RedisConnectionFactory redisConnectionFactory)   {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> containerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder().pollTimeout(Duration.ofMillis(100)).build();
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container = StreamMessageListenerContainer.create(redisConnectionFactory,
                containerOptions);
        container.start();
        Subscription subscription = container.receive(Consumer.from("cg1", "a"), StreamOffset.fromStart("stm"), streamListener);
        System.out.println(subscription.isActive());
        return container.receive(StreamOffset.fromStart("n:e"), streamListener);
    }


}
