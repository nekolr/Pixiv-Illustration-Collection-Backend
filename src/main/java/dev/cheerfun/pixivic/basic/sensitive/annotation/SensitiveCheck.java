package dev.cheerfun.pixivic.basic.sensitive.annotation;


import java.lang.annotation.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019-12-08 15:07
 * @description 敏感词校验注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SensitiveCheck {
    /**
     * 默认为0级权限(即需要登录)，设置时应使用AuthLevel的常量属性
     */
    String value() default "";
}
