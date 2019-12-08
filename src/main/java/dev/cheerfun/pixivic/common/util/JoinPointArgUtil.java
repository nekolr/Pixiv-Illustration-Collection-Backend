package dev.cheerfun.pixivic.common.util;

import dev.cheerfun.pixivic.common.util.dto.JoinPointArg;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/17 23:37
 * @description CommonUtils
 */
@Component
public class JoinPointArgUtil {
    public String getFirstMethodArgByAnnotationValueMethodValue(JoinPoint joinPoint, Class argAnnotationClass, String annotationMethodValue) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return (String) getJoinPointArgsByAnnotation(joinPoint, argAnnotationClass, "value", annotationMethodValue).get(0).getValue();
    }
    public List<JoinPointArg> getMethodArgsByAnnotationValueMethodValue(JoinPoint joinPoint, Class argAnnotationClass) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return getJoinPointArgsByAnnotation(joinPoint, argAnnotationClass, null, null);
    }

    public List<JoinPointArg> getJoinPointArgsByAnnotation(JoinPoint joinPoint, Class argAnnotationClass, String annotationMethodName, String annotationMethodValue) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<JoinPointArg> argList = new ArrayList<>();
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        assert args.length == parameterAnnotations.length;
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            for (Annotation annotation : parameterAnnotations[argIndex]) {
                if (!annotation.annotationType().equals(argAnnotationClass)) {
                    continue;
                }
                if(annotationMethodName!=null&&annotationMethodValue!=null){
                    Method m = argAnnotationClass.getMethod(annotationMethodName);
                    if (!annotationMethodValue.equals(m.invoke(annotation))) {
                        continue;
                    }
                }
                argList.add(new JoinPointArg(argIndex,args[argIndex]));
            }
        }
        return argList;
    }

    public void setJoinPointArgsByAnnotation(JoinPoint joinPoint, Object targetValue, Class argAnnotationClass, String annotationMethodName, String annotationMethodValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        assert args.length == parameterAnnotations.length;
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            for (Annotation annotation : parameterAnnotations[argIndex]) {
                if (!annotation.annotationType().equals(argAnnotationClass)) {
                    continue;
                }
                if (annotationMethodName!=null&&annotationMethodValue!=null){
                    Method m = argAnnotationClass.getMethod(annotationMethodName);
                    if (!annotationMethodValue.equals(m.invoke(annotation))) {
                        continue;
                    }
                }
                args[argIndex] = targetValue;
            }
        }
    }
}
