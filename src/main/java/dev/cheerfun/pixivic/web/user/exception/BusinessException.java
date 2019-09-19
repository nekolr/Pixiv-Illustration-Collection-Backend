package dev.cheerfun.pixivic.web.user.exception;

import dev.cheerfun.pixivic.common.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/19 22:08
 * @description BusinessException
 */
@Data
@AllArgsConstructor
public class BusinessException extends BaseException {
    private HttpStatus httpStatus;
    private String message;
}
