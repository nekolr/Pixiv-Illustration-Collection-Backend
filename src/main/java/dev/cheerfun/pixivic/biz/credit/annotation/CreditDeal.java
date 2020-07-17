package dev.cheerfun.pixivic.biz.credit.annotation;

import dev.cheerfun.pixivic.biz.credit.constant.CreditOption;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CreditDeal {
    int value() default 0;

    int option() default CreditOption.INCREASE;//加分扣分

    boolean isRandom() default false;//是否随机

    int randomStart();

    int randomEnd();

    int limit() default -1;//每天限制多少次
}