package dev.cheerfun.pixivic.auth.exception;

import dev.cheerfun.pixivic.common.constant.StatusCode;
import dev.cheerfun.pixivic.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/18 14:08
 * @description token失效异常
 */
public class AuthExpirationException extends BaseException {
    public AuthExpirationException(StatusCode statusCode, HttpStatus httpStatus) {
        super(statusCode, httpStatus);
    }
}
