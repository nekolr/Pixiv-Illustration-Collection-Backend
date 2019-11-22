package dev.cheerfun.pixivic.basic.auth.exception;

import dev.cheerfun.pixivic.common.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 10:44
 * @description AuthBanException
 */
@Data
@AllArgsConstructor
public class AuthBanException  extends BaseException {
    private HttpStatus httpStatus;
    private String message;
}
