package dev.cheerfun.pixivic.common.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/17 23:37
 * @description CommonUtils
 */
@Component
public class CommonUtil {
    public String getControllerArg(JoinPoint joinPoint, Class argAnnotationType, String argAnnotationValue) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return (String) getControllerArg(joinPoint, String.class, argAnnotationType, argAnnotationValue);
    }

    public Object getControllerArg(JoinPoint joinPoint, Class argClass, Class argAnnotationType, String argAnnotationValue) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        assert args.length == parameterAnnotations.length;
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            for (Annotation annotation : parameterAnnotations[argIndex]) {
                if (!annotation.annotationType().equals(argAnnotationType)) {
                    continue;
                }
                Method m =  argAnnotationType.getMethod("value");
                if (!argAnnotationValue.equals( m.invoke(annotation))) {
                    continue;
                }
                return argClass.cast(args[argIndex]);
            }
        }
        return null;
    }
    public void setControllerArg(JoinPoint joinPoint, String targetValue, Class argAnnotationType, String argAnnotationValue) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        assert args.length == parameterAnnotations.length;
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            for (Annotation annotation : parameterAnnotations[argIndex]) {
                if (!annotation.annotationType().equals(argAnnotationType)) {
                    continue;
                }
                switch (argAnnotationType.getSimpleName()) {
                    case "RequestHeader": {
                        RequestHeader requestHeader = (RequestHeader) annotation;
                        if (!argAnnotationValue.equals(requestHeader.value())) {
                            continue;
                        }
                        break;
                    }
                    case "RequestParam": {
                        RequestParam requestParam = (RequestParam) annotation;
                        if (!argAnnotationValue.equals(requestParam.value())) {
                            continue;
                        }
                        break;
                    }
                    case "PathVariable": {
                        PathVariable pathVariable = (PathVariable) annotation;
                        if (!argAnnotationValue.equals(pathVariable.value())) {
                            continue;
                        }
                        break;
                    }
                    default:
                        break;
                }
                 args[argIndex]=targetValue;
            }
        }
    }
}
