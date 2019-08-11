package dev.cheerfun.pixivic.auth.annotation;

import dev.cheerfun.pixivic.auth.constant.PermissionLevel;

import java.lang.annotation.*;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 10:41
 * @description 自定义权限认证注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PermissionRequired {
    /**
     * 默认为0级权限(即需要登录)，设置时应使用AuthLevel的常量属性
     */
    int value() default PermissionLevel.LOGGED;
}
