package dev.cheerfun.pixivic.common.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.search.exception.JsonCastException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/15 20:29
 * @description JsonBodyHandler
 */
@AllArgsConstructor
public class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {
    private static final ObjectMapper objectMapper;
    private final Class<T> type;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> JsonBodyHandler jsonBodyHandler(final Class<T> type) {
        return new JsonBodyHandler(type);
    }


    @Override
    public HttpResponse.BodySubscriber<T> apply(final HttpResponse.ResponseInfo responseInfo) {
        return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofByteArray(),
                byteArray -> {
                    try {
                        return objectMapper.readValue(new ByteArrayInputStream(byteArray), this.type);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //json转换异常
                    throw new JsonCastException(HttpStatus.INTERNAL_SERVER_ERROR,"Json转换出错");
                });
    }
}