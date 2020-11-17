package dev.cheerfun.pixivic.basic.ratelimit.aop;

import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.ratelimit.exception.RateLimitException;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.util.aop.JoinPointArgUtil;
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
import org.springframework.web.bind.annotation.RequestHeader;
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
    private final JoinPointArgUtil joinPointArgUtil;

    private final Map<Integer, Bucket> userBuckets = new ConcurrentHashMap<>();
    private final Map<Integer, Bucket> ipAddrBuckets = new ConcurrentHashMap<>();

    //未登录用户
    private static Bucket freeBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(600, Refill.intervally(600, Duration.ofMinutes(10))))
                // .addLimit(Bandwidth.classic(20, Refill.intervally(20, Duration.ofSeconds(20))))
                .build();
    }

    private static Bucket standardBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(1200, Refill.intervally(1200, Duration.ofMinutes(10))))
                .addLimit(Bandwidth.classic(120, Refill.intervally(120, Duration.ofSeconds(20))))
                .build();
    }

    private static Bucket emailCheckBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(1500, Refill.intervally(1500, Duration.ofMinutes(10))))
                .addLimit(Bandwidth.classic(120, Refill.intervally(120, Duration.ofSeconds(20))))
                .build();
    }

    private static Bucket premiumBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(3200, Refill.intervally(3200, Duration.ofMinutes(10))))
                .addLimit(Bandwidth.classic(320, Refill.intervally(320, Duration.ofSeconds(20))))
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
            Integer permissionLevel = (Integer) AppContext.get().get(AuthConstant.PERMISSION_LEVEL);
            if (permissionLevel == PermissionLevel.EMAIL_CHECKED) {
                requestBucket = this.userBuckets.computeIfAbsent(userId, key -> emailCheckBucket());
            } else if (permissionLevel == PermissionLevel.VIP) {
                requestBucket = this.userBuckets.computeIfAbsent(userId, key -> premiumBucket());
            } else {
                requestBucket = this.userBuckets.computeIfAbsent(userId, key -> standardBucket());
            }
            ConsumptionProbe probe = requestBucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                return joinPoint.proceed();
            }
            //如果超出 redis中递增次数
            log.info("用户:" + userId + "触发限流机制");
            if (stringRedisTemplate.opsForHash().increment(RedisKeyConstant.ACCOUNT_BAN_COUNT_MAP, String.valueOf(userId), 1) > 25) {
                log.info("用户:" + userId + "触发限流机制过多，进行屏蔽");
                //数据库修改屏蔽
                adminService.banUser(userId);
            }
        } else {
            //获取真实ip
            String xForwardedFor = joinPointArgUtil.getFirstMethodArgByAnnotationValueMethodValue(joinPoint, RequestHeader.class, AuthConstant.X_FORWARDED_FOR);
            int i = xForwardedFor.indexOf(",");
            //ip地址转int节省内存
            int ip = ip2Int(xForwardedFor.substring(0, i));
            requestBucket = this.ipAddrBuckets.computeIfAbsent(ip, key -> freeBucket());
            ConsumptionProbe probe = requestBucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                return joinPoint.proceed();
            }
        }

/*        if (userId != null && permissionLevel != null) {

        }*/

        throw new RateLimitException(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁");
    }

    public static int ip2Int(String ipString) {
        // 取 ip 的各段
        String[] ipSlices = ipString.split("\\.");
        int rs = 0;
        for (int i = 0; i < ipSlices.length; i++) {
            // 将 ip 的每一段解析为 int，并根据位置左移 8 位
            int intSlice = Integer.parseInt(ipSlices[i]) << 8 * i;
            // 或运算
            rs = rs | intSlice;
        }
        return rs;
    }
}
