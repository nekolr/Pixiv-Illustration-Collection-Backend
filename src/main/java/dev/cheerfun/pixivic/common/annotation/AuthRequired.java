package dev.cheerfun.pixivic.common.annotation;

import java.lang.annotation.*;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 10:41
 * @description 自定义权限认证注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthRequired {
    /**
     * "*"代表拥有全部权限
     *
     * @return
     */
    String roleName() default "*";
}
