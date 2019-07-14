package dev.cheerfun.pixivic.aspect;

import dev.cheerfun.pixivic.infrastructure.annotation.AuthRequired;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 11:50
 * @description 监听权限拦截
 */
@Aspect
@Component
@Slf4j
public class AuthRequiredListener {

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.infrastructure.annotation.AuthRequired)")
    public void pointCut(){}

    @After(value = "pointCut()")
    public void handleAuthority(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AuthRequired annotation = signature.getMethod().getAnnotation(AuthRequired.class);

        //TODO 将角色和token做比较
        System.out.println(annotation.roleName());
    }

}
