package dev.cheerfun.pixivic.auth.aop;

import dev.cheerfun.pixivic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.auth.exception.AuthBanException;
import dev.cheerfun.pixivic.auth.exception.AuthLevelException;
import dev.cheerfun.pixivic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.common.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.lang.reflect.Method;
import java.util.Map;

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
    private final static String IS_BAN = "isBan";
    private final static String AUTHORIZATION = "Authorization";
    private final static String PERMISSION_LEVEL = "permissionLevel";
    private final static String NEW_TOKEN = "newToken";

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.auth.annotation.PermissionRequired)||@within(dev.cheerfun.pixivic.auth.annotation.PermissionRequired)")
    public void pointCut() {
    }

    @Around(value = "pointCut()")
    public ResponseEntity handleAuthorityBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
        Method method = signature.getMethod();
        //取出token
        String token = commonUtil.getControllerArg(joinPoint, RequestHeader.class, AUTHORIZATION);
        /*进行jwt校验，成功则将User放进ThreadLocal（token即将过期则将刷新后的token放入Map）
        过期则抛出自定义未授权过期异常*/
        Map<String, Object> claims = jwtUtil.validateToken(token);
        if ((Integer) claims.get(IS_BAN) == 0) {
            throw new AuthBanException(HttpStatus.FORBIDDEN, "账户异常");
        }
        //获取具体权限级别
        PermissionRequired methodPermissionRequired = AnnotationUtils.findAnnotation(method, PermissionRequired.class);
        PermissionRequired classPermissionRequired = AnnotationUtils.findAnnotation(method.getDeclaringClass(), PermissionRequired.class);
        int authLevel = methodPermissionRequired != null ? methodPermissionRequired.value() : classPermissionRequired.value();
        if ((Integer) claims.get(PERMISSION_LEVEL) < authLevel) {
            throw new AuthLevelException(HttpStatus.FORBIDDEN, "用户权限不足");
        }
        ResponseEntity response = (ResponseEntity) joinPoint.proceed();
        //直接修改返回值的token为更新后的token
        if (claims.get(NEW_TOKEN) != null) {
            response = ResponseEntity.status(response.getStatusCode())
                    .header("Authorization", String.valueOf(claims.get(NEW_TOKEN)))
                    .body(response.getBody());
        }
        return response;
    }
}