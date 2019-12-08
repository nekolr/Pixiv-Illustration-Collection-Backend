package dev.cheerfun.pixivic.basic.sensitive.annotation;

import java.lang.annotation.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019-12-08 15:07
 * @description 敏感词校验注解
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SensitiveCheck {
}
