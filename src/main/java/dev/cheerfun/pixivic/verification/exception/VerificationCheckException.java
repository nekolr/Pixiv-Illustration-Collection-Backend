package dev.cheerfun.pixivic.verification.exception;

import dev.cheerfun.pixivic.common.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/18 14:03
 * @description 验证码校验异常
 */
@Data
@AllArgsConstructor
public class VerificationCheckException extends BaseException {
    private HttpStatus httpStatus;
    private String message;
}
