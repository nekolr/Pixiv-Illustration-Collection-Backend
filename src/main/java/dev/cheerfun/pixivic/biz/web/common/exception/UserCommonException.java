package dev.cheerfun.pixivic.biz.web.common.exception;

import dev.cheerfun.pixivic.common.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/23 23:21
 * @description CommonException
 */
@Data
@AllArgsConstructor
public class UserCommonException extends BaseException {
    private HttpStatus httpStatus;
    private String message;
}
