package dev.cheerfun.pixivic.verification.annotation;

import dev.cheerfun.pixivic.verification.constant.VerificationType;

import java.lang.annotation.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/16 11:37
 * @description 验证码校验注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckVerification {
    /**
     * 默认为图形验证码，设置时应使用VerificationType的常量属性
     */
    String value() default VerificationType.IMG;
}
