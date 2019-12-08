package dev.cheerfun.pixivic.common.exception.handler;

import dev.cheerfun.pixivic.common.exception.BaseException;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.mail.SendFailedException;
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

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Result> handleBaseException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("参数错误"));
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Result> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result(e.getMessage()));
    }

    @ExceptionHandler(value = SendFailedException.class)
    public ResponseEntity<Result> handleConstraintViolationException(SendFailedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("邮箱地址错误"));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Result> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("参数错误"));
    }

    /*    @ExceptionHandler(Exception.class)//可以用来找异常类
        public ResponseEntity handleException(Exception ae) {
            System.out.println(ae);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result<>("登录异常"));
            // return null;
        }*/

}
