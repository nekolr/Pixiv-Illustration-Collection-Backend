package dev.cheerfun.pixivic.basic.verification.aop;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.verification.annotation.CheckVerification;
import dev.cheerfun.pixivic.basic.verification.constant.VerificationType;
import dev.cheerfun.pixivic.basic.verification.exception.VerificationCheckException;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.util.aop.JoinPointArgUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    private final JoinPointArgUtil commonUtil;
    private final StringRedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(dev.cheerfun.pixivic.basic.verification.annotation.CheckVerification)")
    public void check() {
    }

    @Before("check()")
    public void checkVerification(JoinPoint joinPoint) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
        Method method = signature.getMethod();
        //获取具体验证码类型
        CheckVerification checkVerification = AnnotationUtils.findAnnotation(method, CheckVerification.class);
        String redisPre = checkVerification.value().equals(VerificationType.MESSAGE) ? RedisKeyConstant.MESSAGE_VERIFICATION_CODE : RedisKeyConstant.VERIFICATION_CODE;
        //获取共有value属性，没有就抛异常
        //其实这两个都不会为空，若为空在controller之前就会被拦截
        String value = commonUtil.getFirstMethodArgByAnnotationValueMethodValue(joinPoint, RequestParam.class, "value");
        String vid = commonUtil.getFirstMethodArgByAnnotationValueMethodValue(joinPoint, RequestParam.class, "vid");
        String v = stringRedisTemplate.opsForValue().get(redisPre + vid);
        if (v == null) {
            throw new VerificationCheckException(HttpStatus.NOT_FOUND, "验证码已过期或不存在");
        }
        //进数据库查询
        if (!value.equalsIgnoreCase(v)) {
            throw new VerificationCheckException(HttpStatus.BAD_REQUEST, "验证码错误");
        }
        stringRedisTemplate.delete(redisPre + vid);
        //放行
    }

}
