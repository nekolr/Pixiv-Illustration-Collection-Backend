package dev.cheerfun.pixivic.verification.exception;

import dev.cheerfun.pixivic.common.constant.StatusCode;
import dev.cheerfun.pixivic.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/18 14:03
 * @description 验证码校验异常
 */
public class VerificationCheckException extends BaseException {
    public VerificationCheckException(StatusCode statusCode, HttpStatus httpStatus) {
        super(statusCode, httpStatus);
    }
}
