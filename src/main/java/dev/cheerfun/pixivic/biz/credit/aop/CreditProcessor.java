package dev.cheerfun.pixivic.biz.credit.aop;

import dev.cheerfun.pixivic.biz.credit.annotation.CreditDeal;
import dev.cheerfun.pixivic.biz.credit.mapper.CreditMapper;
import dev.cheerfun.pixivic.biz.credit.po.CreditHistory;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/26 3:24 下午
 * @description CreditProcessor
 */
//@Aspect
//@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Order(5)
public class CreditProcessor {
    private final CreditMapper creditMapper;

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.biz.credit.annotation.CreditDeal)||@within(dev.cheerfun.pixivic.biz.credit.annotation.CreditDeal)")
    public void pointCut() {
    }

    @Around(value = "pointCut()")
    @Transactional
    public Object handleCredit(ProceedingJoinPoint joinPoint) throws Throwable {
        //扣分使用aop 加分用事件
        //获取注解参数
        MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
        Method method = signature.getMethod();
        //获取具体处理信息
        CreditDeal creditDeal = AnnotationUtils.findAnnotation(method, CreditDeal.class);
        //获取userId
        Map<String, Object> context = AppContext.get();
        if (context != null && context.get(AuthConstant.USER_ID) != null) {
            //校验分数是否足够
            Integer userId = (Integer) context.get(AuthConstant.USER_ID);
            if (checkCredit(userId, creditDeal.value())) {
                //积分处理并且持久化
                creditMapper.insertCreditLog(new CreditHistory(null, userId, null, null, null, 0, creditDeal.value(), creditDeal.desc(), null));
                creditMapper.increaseUserScore(userId, creditDeal.value());
                //放行
                Object proceed = joinPoint.proceed();
                return proceed;
            }
        }
        //积分不足
        throw new BusinessException(HttpStatus.FORBIDDEN, "积分不足");
    }

    private boolean checkCredit(Integer userId, Integer cost) {
        return creditMapper.queryUserScore(userId).compareTo(cost) > 0;
    }

}
