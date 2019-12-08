package dev.cheerfun.pixivic.basic.ratelimit.annotation;

import java.lang.annotation.*;

/**
 * @author OysterQAQ
 * @version 2.0
 * @date 2019-11-22 15:07
 * @description 限流注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

}
