package dev.cheerfun.pixivic.biz.credit.config;

import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import org.springframework.amqp.core.*;
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
    FanoutExchange creditExchange() {
        return new FanoutExchange("creditExchange");
    }

    @Bean
    public Binding bindFanoutExchange(FanoutExchange fanoutExchange, FanoutExchange creditExchange) {
        return BindingBuilder.bind(creditExchange).to(fanoutExchange);
    }

    @Bean("creditQueue")
    public Queue creditQueue() {
        return new Queue("creditQueue");
    }

    @Bean
    public Binding creditExchangeBindCreditQueue(Queue creditQueue, FanoutExchange creditExchange) {
        return BindingBuilder.bind(creditQueue).to(creditExchange);
    }
}
