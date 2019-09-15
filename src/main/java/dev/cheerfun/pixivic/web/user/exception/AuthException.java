package dev.cheerfun.pixivic.web.user.exception;

import dev.cheerfun.pixivic.common.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 14:24
 * @description UserAuthException
 */
@AllArgsConstructor
@Data
public class AuthException extends BaseException {
    private HttpStatus httpStatus;
    private String message;
}
