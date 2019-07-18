package dev.cheerfun.pixivic.verification.aop;

import dev.cheerfun.pixivic.common.util.CommonUtil;
import dev.cheerfun.pixivic.verification.exception.VerificationCheckException;
import dev.cheerfun.pixivic.verification.model.AbstractVerificationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

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
    private final CommonUtil commonUtil;
    private final StringRedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(dev.cheerfun.pixivic.verification.annotation.CheckVerification)")
    public void check() {
    }

    @Pointcut("execution(* dev.cheerfun.pixivic.verification.util.VerificationCodeBuildUtil.build(..))")
    public void generate() {
    }

    @Before("check()")
    public void checkVerification(JoinPoint joinPoint) {
        //获取共有value属性，没有就抛异常
        //其实这两个都不会为空，若为空在controller之前就会被拦截
        String value = commonUtil.getControllerArg(joinPoint, RequestParam.class, "value");
        String vid = commonUtil.getControllerArg(joinPoint, RequestParam.class, "vid");
       /* if(value==null||vid==null)
            throw new RuntimeException();*/
        //进数据库查询
        if (!value.equals(stringRedisTemplate.opsForValue().get(vid))) {
            throw new VerificationCheckException();
        }
        //放行
    }

    @AfterReturning(value = "generate()", returning = "code")
    public void putVerificationCodeInRedis(AbstractVerificationCode code) {
        stringRedisTemplate.opsForValue().set(code.getVid(), code.getValue());
    }
}
