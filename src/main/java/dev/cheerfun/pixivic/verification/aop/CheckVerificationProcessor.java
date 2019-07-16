package dev.cheerfun.pixivic.verification.aop;

import dev.cheerfun.pixivic.verification.util.VerificationCodeCheckUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/15 22:24
 * @description aop权限校验处理
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CheckVerificationProcessor {
    private final VerificationCodeCheckUtil verificationCodeCheckUtil;

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.verification.annotation.CheckVerification)")
    public void pointCut() {
    }

    @Before(value = "pointCut()")
    public void handleAuthority(JoinPoint joinPoint) {
        //获取共有value属性，没有就抛异常

    }
}
