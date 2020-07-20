package dev.cheerfun.pixivic.biz.credit.annotation;

import dev.cheerfun.pixivic.biz.credit.constant.CreditOption;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CreditDeal {
    int value() default 0;

    String desc();//描述
}