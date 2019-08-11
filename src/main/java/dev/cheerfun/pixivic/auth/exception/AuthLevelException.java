package dev.cheerfun.pixivic.auth.exception;

import dev.cheerfun.pixivic.common.constant.StatusCode;
import dev.cheerfun.pixivic.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/18 14:07
 * @description 权限不足异常
 */
public class AuthLevelException extends BaseException {
    public AuthLevelException(StatusCode statusCode, HttpStatus httpStatus) {
        super(statusCode, httpStatus);
    }
}
