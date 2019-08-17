package dev.cheerfun.pixivic.common.exception;

import dev.cheerfun.pixivic.web.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author OysterQAQ
 * @version 2.0
 * @date 2019-08-16 14:17
 * @description 全局异常捕获
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<Result> handleBizException(BaseException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new Result(e.getMessage()));
    }
}
