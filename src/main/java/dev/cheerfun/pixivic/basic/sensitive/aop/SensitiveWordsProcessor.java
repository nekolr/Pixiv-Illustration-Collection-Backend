package dev.cheerfun.pixivic.basic.sensitive.aop;

import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.basic.sensitive.util.SensitiveFilter;
import dev.cheerfun.pixivic.common.util.JoinPointArgUtil;
import dev.cheerfun.pixivic.common.util.dto.JoinPointArg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-8 下午4:55
 * @description SensitiveWordsProcessor
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SensitiveWordsProcessor {
    private final JoinPointArgUtil commonUtil;
    private final SensitiveFilter sensitiveFilter;

    @Pointcut(value = "execution(public * *(.., @dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck (*), ..))")
    public void pointCutInMethodParam() {
    }

    @Around(value = "pointCutInMethodParam()")
    private Object handleSensitiveCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        List<JoinPointArg> contentStream = commonUtil.getMethodArgsByAnnotationValueMethodValue(joinPoint, SensitiveCheck.class);
        contentStream.forEach(e -> {
            try {
                joinPoint.getArgs()[e.getIndex()] = sensitiveFilter.filter(e.getValue());
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        });
        return joinPoint.proceed(joinPoint.getArgs());
    }

}
