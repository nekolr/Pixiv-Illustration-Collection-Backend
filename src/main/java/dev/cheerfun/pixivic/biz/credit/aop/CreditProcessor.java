package dev.cheerfun.pixivic.biz.credit.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/26 3:24 下午
 * @description CreditProcessor
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Order(5)
public class CreditProcessor {
    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.biz.credit.annotation.CreditDeal)||@within(dev.cheerfun.pixivic.biz.credit.annotation.CreditDeal)")
    public void pointCut() {
    }

    @Around(value = "pointCut()")
    public Object handleCredit(ProceedingJoinPoint joinPoint) {
        //扣分使用aop 加分用事件
        //获取注解参数
        //获取userId
        //积分处理并且持久化
        return null;
    }
}
