package dev.cheerfun.pixivic.biz.event.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/17 4:18 下午
 * @description RabbitmqConfig
 */
@Configuration
public class RabbitmqConfig {
    @Bean("fanoutExchange")
    FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanoutExchange");//配置广播路由器
    }

}
