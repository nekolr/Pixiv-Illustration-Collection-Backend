package dev.cheerfun.pixivic.ratelimit.exception;

import dev.cheerfun.pixivic.common.exception.BaseException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/11/22 15:39
 * @description RateLimitException
 */
@AllArgsConstructor
public class RateLimitException extends BaseException {
    private HttpStatus httpStatus;
    private String message;
}
