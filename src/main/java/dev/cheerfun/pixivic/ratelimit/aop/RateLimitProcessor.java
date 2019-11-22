package dev.cheerfun.pixivic.ratelimit.aop;

import dev.cheerfun.pixivic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.ratelimit.exception.RateLimitException;
import io.github.bucket4j.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author OysterQAQ
 * @version 2.0
 * @date 2019-11-22 15:12
 * @description 限流处理器
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Order(1)
public class RateLimitProcessor implements HandlerInterceptor {
    private static final String USER_ID = "userId";
    private final static String PERMISSION_LEVEL = "permissionLevel";
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    //未登录用户
    private final Bucket freeBucket = Bucket4j.builder()
            .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
            .build();

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.ratelimit.annotation.RateLimit)||@within(dev.cheerfun.pixivic.ratelimit.annotation.RateLimit)")
    public void pointCut() {
    }

    @Around(value = "pointCut()")
    public ResponseEntity handleRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        Bucket requestBucket;
        if (AppContext.get() != null) {
            Integer userId = (Integer) AppContext.get().get(USER_ID);
            Integer permissionLevel = (Integer) AppContext.get().get(PERMISSION_LEVEL);
            if (permissionLevel == PermissionLevel.VIP) {
                requestBucket = this.buckets.computeIfAbsent(userId.toString(), key -> premiumBucket());
            } else {
                requestBucket = this.buckets.computeIfAbsent(userId.toString(), key -> standardBucket());
            }
        } else {
            requestBucket = this.freeBucket;
        }

/*        if (userId != null && permissionLevel != null) {

        }*/
        ConsumptionProbe probe = requestBucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            return (ResponseEntity) joinPoint.proceed();
        }
        throw new RateLimitException(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁");
    }

    private static Bucket standardBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1))))
                .build();
    }

    private static Bucket premiumBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
                .build();
    }
}
