package dev.cheerfun.pixivic.biz.credit.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/17 9:31 下午
 * @description CreditRabbitmqConfig
 */
@Configuration
public class CreditRabbitmqConfig {
    @Bean("creditExchange")
    DirectExchange notifyExchange() {
        return new DirectExchange("creditExchange");
    }

}
