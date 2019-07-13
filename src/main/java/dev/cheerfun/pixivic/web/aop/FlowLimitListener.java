package dev.cheerfun.pixivic.web.aop;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import dev.cheerfun.pixivic.common.annotation.FlowLimit;
import dev.cheerfun.pixivic.common.context.enums.CodeEnum;
import dev.cheerfun.pixivic.web.execption.VisitOftenException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 15:12
 * @description 限流监听器
 */
@Aspect
@Component
@Slf4j
public class FlowLimitListener {

    /**
     * 用来存放不同接口的RateLimiter(key为接口名称，value为RateLimiter)
     */
    private Map<String, RateLimiter> map = Maps.newConcurrentMap();
    private RateLimiter rateLimiter;

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.common.annotation.FlowLimit)")
    public void pointCut(){}

    @Around(value = "pointCut()")
    public Object handleRateLimiter(ProceedingJoinPoint joinPoint){
        Object obj=null;
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        FlowLimit annotation = methodSignature.getMethod().getAnnotation(FlowLimit.class);
        double limitNum = annotation.limitNum();
        String functionName = methodSignature.getName();
        if (map.containsKey(functionName)) {
            rateLimiter = map.get(functionName);
        } else {
            //每秒允许多少请求limitNum
            map.put(functionName, RateLimiter.create(limitNum));
            rateLimiter = map.get(functionName);
        }

        try {
            if (rateLimiter.tryAcquire()) {
                //执行方法
                obj = joinPoint.proceed();
            } else {
                //拒绝了请求（服务降级）
                //TODO 拒绝后提示
                throw new VisitOftenException(CodeEnum.VISIT_OFTEN.getCode(),CodeEnum.VISIT_OFTEN.getMsg());
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return obj;
    }
}
