package dev.cheerfun.pixivic.web.search.exception;

import dev.cheerfun.pixivic.common.exception.BaseException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/16 15:08
 * @description JsonCastException
 */
@AllArgsConstructor
public class JsonCastException extends BaseException {
    private HttpStatus httpStatus;
    private String message;
}
