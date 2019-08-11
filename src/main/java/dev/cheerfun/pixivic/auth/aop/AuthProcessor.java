package dev.cheerfun.pixivic.auth.aop;

import dev.cheerfun.pixivic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.auth.exception.AuthLevelException;
import dev.cheerfun.pixivic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.common.constant.StatusCode;
import dev.cheerfun.pixivic.common.model.User;
import dev.cheerfun.pixivic.common.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

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
public class AuthProcessor {
    private final JWTUtil jwtUtil;
    private final CommonUtil commonUtil;

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.auth.annotation.PermissionRequired)||@within(dev.cheerfun.pixivic.auth.annotation.PermissionRequired)")
    public void pointCut() {
    }

    @Before(value = "pointCut()")
    public void handleAuthority(JoinPoint joinPoint) {
        MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
        Method method = signature.getMethod();
        //取出token
        String token = commonUtil.getControllerArg(joinPoint, RequestHeader.class, "Authorization");
        /*进行jwt校验，成功则将User放进ThreadLocal（token即将过期则将刷新后的token放入ThreadLocal）
        过期则抛出自定义未授权过期异常*/
        User user = jwtUtil.validateToken(token);
        //获取具体权限级别
        PermissionRequired methodPermissionRequired = AnnotationUtils.findAnnotation(method, PermissionRequired.class);
        PermissionRequired classPermissionRequired = AnnotationUtils.findAnnotation(method.getDeclaringClass(), PermissionRequired.class);
        int authLevel = methodPermissionRequired != null ? methodPermissionRequired.value() : classPermissionRequired.value();
        if (user.getLevel() < authLevel) {
            throw new AuthLevelException(StatusCode.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
        }
    }

}