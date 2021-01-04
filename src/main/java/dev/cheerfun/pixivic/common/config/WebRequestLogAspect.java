package dev.cheerfun.pixivic.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/4 4:56 PM
 * @description WebRequestLogAspect
 */

@Aspect
@Component
public class WebRequestLogAspect {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(public * *.controller.*.*(..))")
    public void wsLog() {
    }

    /**
     * 接收到请求，记录请求内容
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Before("wsLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        if (log.isInfoEnabled()) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Map<String, String[]> parameters = request.getParameterMap();
                try {
                    String parametersString = null;
                    String requestBody = null;
                    if (parameters != null) {
                        parametersString = multiValueMapToJsonString(parameters);
                    }
                    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                    //获取被拦截的方法
                    Method method = signature.getMethod();
                    Object object = getAnnotatedParameterValueRequestBody(method, joinPoint.getArgs());
                    if (object != null) {
                        requestBody = objectToJsonString(object);
                    }
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("\nRequest from = ");
                    stringBuffer.append(request);
                    stringBuffer.append(";\n");
                    stringBuffer.append("uri = ");
                    stringBuffer.append(request.getRequestURL().toString());
                    stringBuffer.append(";\n");
                    stringBuffer.append("request method = ");
                    stringBuffer.append(request.getMethod());
                    stringBuffer.append(";\n");
                    stringBuffer.append("content type = ");
                    stringBuffer.append(request.getContentType());
                    stringBuffer.append(";\n");
                    stringBuffer.append("request parameters = ");
                    stringBuffer.append(parametersString);
                    stringBuffer.append(";\n");
                    stringBuffer.append("request body = ");
                    stringBuffer.append(requestBody);
                    stringBuffer.append(";\n");

                    log.info(stringBuffer.toString());
                    String headers = objectToJsonString(getHeadersInfo(request));
                    log.info("headers:" + headers);
                } catch (Exception e) {
                    log.info("log http request Exception: ", e);
                }
            }
        }
    }

    @AfterReturning(returning = "ret", pointcut = "wsLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        if (log.isInfoEnabled()) {
            try {
                log.info("Response from server : \n" + objectToJsonString(ret));
            } catch (Exception e) {
                log.info("log http response Exception:\n ", e);
            }
        }
    }

    private Object getAnnotatedParameterValueRequestBody(Method method, Object[] args) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        ///Parameter[] parameters = method.getParameters();

        int i = 0;
        for (Annotation[] annotations : parameterAnnotations) {
            Object arg = args[i];
            ///String name = parameters[i++].getDeclaringExecutable().getName();
            for (Annotation annotation : annotations) {
                if (annotation instanceof RequestBody) {
                    return arg;
                }
            }
        }
        return null;
    }

    //get request headers
    private Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        // 美化输出
        ///mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaTimeModule timeModule = new JavaTimeModule();

        timeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        timeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        timeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        timeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        mapper.registerModule(timeModule);
    }

    public static String objectToJsonString(Object object) throws JsonProcessingException {
        //Object to JSON in String
        return mapper.writeValueAsString(object);
    }

    public static String multiValueMapToJsonString(Map<String, String[]> object) throws JsonProcessingException {
        Map<String, String> newMap = new HashMap<>(16);
        if (object != null && object.size() > 0) {
            object.forEach((k, v) -> {
                if (v != null && v.length > 0) {
                    newMap.put(k, Arrays.toString(v));
                }
            });
        }
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        //Object to JSON in String
        return mapper.writeValueAsString(newMap);
    }

    public static <T> T jsonStringToObject(String jsonString, Class<T> t) throws IOException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        //JSON from String to Object
        return mapper.readValue(jsonString, t);
    }

    /**
     * string转map,list等
     * Map<String, List<String>> result = JsonUtil.jsonStringToObject("{\"a\":[1],\"b\":[2],\"c\":[\"d\",\"e\",\"f\"]}", new TypeReference<Map<String, List<String>>>() {
     * });
     *
     * @param str           json字符串
     * @param typeReference 被转对象引用类型
     * @param <T>
     * @return
     */
    public static <T> T jsonStringToObject(String str, TypeReference<T> typeReference) throws IOException {
        return mapper.readValue(str, typeReference);
    }

    /**
     * string转object 用于转为集合对象
     *
     * @param str             json字符串
     * @param collectionClass 被转集合class
     * @param elementClasses  被转集合中对象类型class
     * @param <T>
     * @return
     */
    public static <T> T jsonStringToObject(String str, Class<?> collectionClass, Class<?>... elementClasses) throws IOException {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        return mapper.readValue(str, javaType);
    }
}
