package dev.cheerfun.pixivic.basic.auth.aop;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.auth.exception.AuthBanException;
import dev.cheerfun.pixivic.basic.auth.exception.AuthLevelException;
import dev.cheerfun.pixivic.basic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.util.JoinPointArgUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
@Order(0)
public class AuthProcessor {
    private final JWTUtil jwtUtil;
    private final JoinPointArgUtil commonUtil;
    private final static String IS_BAN = "isBan";
    private final static String AUTHORIZATION = "Authorization";
    private final static String PERMISSION_LEVEL = "permissionLevel";
    private final static String NEW_TOKEN = "newToken";

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired)||@within(dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired)")
    public void pointCut() {
    }

    @Around(value = "pointCut()")
    public Object handleAuthorityBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
        Method method = signature.getMethod();
        //获取具体权限级别
        PermissionRequired methodPermissionRequired = AnnotationUtils.findAnnotation(method, PermissionRequired.class);
        PermissionRequired classPermissionRequired = AnnotationUtils.findAnnotation(method.getDeclaringClass(), PermissionRequired.class);
        int authLevel = methodPermissionRequired != null ? methodPermissionRequired.value() : classPermissionRequired.value();
        //如果不是任何人都能访问的
        if (authLevel != PermissionLevel.ANONYMOUS) {
            //取出token
            String token = commonUtil.getFirstMethodArgByAnnotationValueMethodValue(joinPoint, RequestHeader.class, AUTHORIZATION);
        /*进行jwt校验，成功则将返回包含Claim信息的Map（token即将过期则将刷新后的token放入返回值Map）
        过期则抛出自定义未授权过期异常*/
            Map<String, Object> claims = jwtUtil.validateToken(token);
            if ((Integer) claims.get(IS_BAN) == 0) {
                throw new AuthBanException(HttpStatus.FORBIDDEN, "账户异常");
            }
            if ((Integer) claims.get(PERMISSION_LEVEL) < authLevel) {
                throw new AuthLevelException(HttpStatus.FORBIDDEN, "用户权限不足");
            }
            //放入threadlocal
            AppContext.set(claims);
            Object proceed = joinPoint.proceed();
            //直接修改返回值的token为更新后的token，若之后在业务逻辑中有更改，则在threadlocal中放入NEW_TOKEN就行
/*        if (AppContext.get().get(NEW_TOKEN) != null) {
            response = ResponseEntity.status(response.getStatusCode())
                    .header(AUTHORIZATION, String.valueOf(claims.get(NEW_TOKEN)))
                    .body(response.getBody());
        }*/
            if (proceed instanceof CompletableFuture) {
                CompletableFuture<ResponseEntity> response = (CompletableFuture<ResponseEntity>) joinPoint.proceed();
                return response.thenApply(e -> dealReturn(e, claims));
            }
            return dealReturn((ResponseEntity) proceed, claims);
        } else {
            Object proceed = joinPoint.proceed();
            return proceed;
        }

    }

    private ResponseEntity dealReturn(ResponseEntity responseEntity, Map<String, Object> claims) {
        if (AppContext.get().get(NEW_TOKEN) != null) {
            responseEntity = ResponseEntity.status(responseEntity.getStatusCode())
                    .header(AUTHORIZATION, String.valueOf(claims.get(NEW_TOKEN)))
                    .body(responseEntity.getBody());
        }
        return responseEntity;
    }
}