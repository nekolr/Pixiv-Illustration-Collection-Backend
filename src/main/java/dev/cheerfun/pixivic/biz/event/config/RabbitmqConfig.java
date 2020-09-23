package dev.cheerfun.pixivic.biz.event.config;

import dev.cheerfun.pixivic.biz.event.constant.RabbitmqConstant;
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
    @Bean(RabbitmqConstant.FANOUT_EXCHANGE)
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(RabbitmqConstant.FANOUT_EXCHANGE);//配置广播路由器
    }

}
