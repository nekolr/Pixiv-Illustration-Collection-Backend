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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

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
    public void pointCut() {
    }

    @Around(value = "pointCut()")
    private ResponseEntity handleSensitiveCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        List<JoinPointArg> contentStream = commonUtil.getMethodArgsByAnnotationValueMethodValue(joinPoint, SensitiveCheck.class);

        contentStream.forEach(e -> {
                    String result = sensitiveFilter.filter((String) e.getValue());
                    if (result.contains("*")) {
                        joinPoint.getArgs()[e.getIndex()] = result;
                        //TODO (异步)发现含有敏感词则进行一定的降权
                    }
                }
        );
        return (ResponseEntity) joinPoint.proceed(joinPoint.getArgs());
    }

}
