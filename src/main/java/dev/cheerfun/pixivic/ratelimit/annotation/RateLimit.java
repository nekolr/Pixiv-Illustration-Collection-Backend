package dev.cheerfun.pixivic.ratelimit.annotation;

import java.lang.annotation.*;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 15:07
 * @description 限流注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    /**
     * 默认每秒放进桶中的令牌
     * @return double
     */
    double limitNum() default 20.0;
}
