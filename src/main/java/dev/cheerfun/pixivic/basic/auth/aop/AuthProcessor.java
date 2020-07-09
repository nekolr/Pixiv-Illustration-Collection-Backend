package dev.cheerfun.pixivic.basic.auth.aop;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.auth.exception.AuthBanException;
import dev.cheerfun.pixivic.basic.auth.exception.AuthLevelException;
import dev.cheerfun.pixivic.basic.auth.mapper.AuthMapper;
import dev.cheerfun.pixivic.basic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

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
    private final StringRedisTemplate stringRedisTemplate;
    private final ExecutorService saveToDBExecutorService;
    private final AuthMapper authMapper;
    private Map<Integer, Integer> waitForUpdateUserList = new ConcurrentHashMap<>(10000 * 10);

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired)||@within(dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired)")
    public void pointCut() {
    }

    @PostConstruct
    public void init() {
        dealWaitForUpdateUserList();
    }

    @Around(value = "pointCut()")
    public Object handleAuthorityBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
        Method method = signature.getMethod();
        //获取具体权限级别
        PermissionRequired methodPermissionRequired = AnnotationUtils.findAnnotation(method, PermissionRequired.class);
        PermissionRequired classPermissionRequired = AnnotationUtils.findAnnotation(method.getDeclaringClass(), PermissionRequired.class);
        int authLevel = methodPermissionRequired != null ? methodPermissionRequired.value() : classPermissionRequired.value();
        String token = commonUtil.getFirstMethodArgByAnnotationValueMethodValue(joinPoint, RequestHeader.class, AuthConstant.AUTHORIZATION);
        /*进行jwt校验，成功则将返回包含Claim信息的Map（token即将过期则将刷新后的token放入返回值Map）
        过期则抛出自定义未授权过期异常*/
        if (token != null) {
            Map<String, Object> claims = jwtUtil.validateToken(token);
            //放入threadlocal
            AppContext.set(claims);
            if ((Integer) claims.get(AuthConstant.IS_BAN) == 0 || stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ACCOUNT_BAN_SET, String.valueOf(claims.get(AuthConstant.USER_ID)))) {
                throw new AuthBanException(HttpStatus.FORBIDDEN, "账户异常");
            }
            if ((Integer) claims.get(AuthConstant.PERMISSION_LEVEL) < authLevel) {
                throw new AuthLevelException(HttpStatus.FORBIDDEN, "用户权限不足");
            }
            //放入等待更新map
            waitForUpdateUserList.put(Integer.parseInt((String) claims.get(AuthConstant.USER_ID)), 1);
            //放行
            Object proceed = joinPoint.proceed();
            //清除Threadlocal中的数据
            AppContext.remove();
            //直接修改返回值的token为更新后的token，若之后在业务逻辑中有更改，则在threadlocal中放入NEW_TOKEN就行
            if (proceed instanceof CompletableFuture) {
                CompletableFuture<ResponseEntity> response = (CompletableFuture<ResponseEntity>) joinPoint.proceed();
                return response.thenApply(e -> dealReturn(e, claims));
            }
            return dealReturn((ResponseEntity) proceed, claims);
        } else {
            if (authLevel == PermissionLevel.ANONYMOUS) {
                Object proceed = joinPoint.proceed();
                return proceed;
            } else {
                throw new AuthBanException(HttpStatus.UNAUTHORIZED, "账户未登录");
            }
        }

    }

    private ResponseEntity dealReturn(ResponseEntity responseEntity, Map<String, Object> claims) {
        if (AppContext.get() != null && AppContext.get().get(AuthConstant.NEW_TOKEN) != null) {
            responseEntity = ResponseEntity.status(responseEntity.getStatusCode())
                    .header(AuthConstant.AUTHORIZATION, String.valueOf(claims.get(AuthConstant.NEW_TOKEN)))
                    .body(responseEntity.getBody());
        }
        return responseEntity;
    }

    public void dealWaitForUpdateUserList() {
        saveToDBExecutorService.submit(() -> {
            while (true) {
                try {
                    if (!waitForUpdateUserList.isEmpty()) {
                        Set<Integer> userSet = waitForUpdateUserList.keySet();
                        waitForUpdateUserList.clear();
                        authMapper.updateUserLastActiveTime(userSet);
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}