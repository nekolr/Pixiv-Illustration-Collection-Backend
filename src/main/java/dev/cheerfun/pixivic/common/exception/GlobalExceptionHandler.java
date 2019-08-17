package dev.cheerfun.pixivic.common.exception;

import dev.cheerfun.pixivic.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

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
    public ResponseEntity<Result> handleBaseException(BaseException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new Result(e.getMessage()));
    }
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Result> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result(e.getMessage()));
    }
}
