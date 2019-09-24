package dev.cheerfun.pixivic.auth.exception;

import dev.cheerfun.pixivic.common.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/18 14:07
 * @description 权限不足异常
 */
@Data
@AllArgsConstructor
public class AuthLevelException extends BaseException {
    private HttpStatus httpStatus;
    private String message;
}
