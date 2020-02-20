package dev.cheerfun.pixivic.basic.userInfo.annotation;

import java.lang.annotation.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020-02-20 14:04
 * @description 附带用户关注信息注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WithUserInfo {

}
