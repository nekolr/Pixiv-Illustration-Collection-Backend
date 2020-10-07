package dev.cheerfun.pixivic.basic.ratelimit.aop;

import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.ratelimit.exception.RateLimitException;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import io.github.bucket4j.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
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
    private final StringRedisTemplate stringRedisTemplate;
    private final AdminService adminService;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    //未登录用户
    private final Bucket freeBucket = Bucket4j.builder()
            .addLimit(Bandwidth.classic(300, Refill.intervally(300, Duration.ofMinutes(1))))
            .build();

    private static Bucket standardBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(300, Refill.intervally(300, Duration.ofMinutes(1))))
                .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofSeconds(20))))
                .build();
    }

    private static Bucket emailCheckBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(300, Refill.intervally(300, Duration.ofMinutes(1))))
                .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofSeconds(20))))
                .build();
    }

    private static Bucket premiumBucket() {
        Bandwidth limit = Bandwidth
                .simple(1800, Duration.ofMinutes(1))
                .withInitialTokens(1800);
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.basic.ratelimit.annotation.RateLimit)||@within(dev.cheerfun.pixivic.basic.ratelimit.annotation.RateLimit)")
    public void pointCut() {
    }

    @Around(value = "pointCut()")
    public Object handleRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        Bucket requestBucket;
        if (AppContext.get() != null && AppContext.get().get(AuthConstant.USER_ID) != null) {
            Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
            System.out.println(userId);
            Integer permissionLevel = (Integer) AppContext.get().get(AuthConstant.PERMISSION_LEVEL);
            if (permissionLevel == PermissionLevel.EMAIL_CHECKED) {
                requestBucket = this.buckets.computeIfAbsent(userId.toString(), key -> emailCheckBucket());
            } else if (permissionLevel == PermissionLevel.VIP) {
                requestBucket = this.buckets.computeIfAbsent(userId.toString(), key -> premiumBucket());
            } else {
                requestBucket = this.buckets.computeIfAbsent(userId.toString(), key -> standardBucket());
            }
            ConsumptionProbe probe = requestBucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                return joinPoint.proceed();
            }
            //如果超出 redis中递增次数
            log.info("用户:" + userId + "触发限流机制");
            if (stringRedisTemplate.opsForHash().increment(RedisKeyConstant.ACCOUNT_BAN_COUNT_MAP, String.valueOf(userId), 1) > 30) {
                log.info("用户:" + userId + "触发限流机制过多，进行屏蔽");
                //数据库修改屏蔽
                adminService.banUser(userId);
            }
        } else {
            requestBucket = this.freeBucket;
            ConsumptionProbe probe = requestBucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                return joinPoint.proceed();
            }
        }

/*        if (userId != null && permissionLevel != null) {

        }*/

        throw new RateLimitException(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁");
    }
}
