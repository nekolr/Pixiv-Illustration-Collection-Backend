package dev.cheerfun.pixivic.common.exception.handler;

import dev.cheerfun.pixivic.common.exception.BaseException;
import dev.cheerfun.pixivic.common.po.Result;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.DecodingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import javax.mail.SendFailedException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

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
    public ResponseEntity<Result> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("输入参数中存在错误", e.getMessage()));
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Result> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("输入参数中存在错误", e.getMessage()));
    }

    @ExceptionHandler(value = SendFailedException.class)
    public ResponseEntity<Result> handleConstraintViolationException(SendFailedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("邮箱地址错误"));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Result> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("参数错误"));
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    public ResponseEntity<Result> handleIllegalExpiredJwtException(ExpiredJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result("登录信息过期"));
    }

    @ExceptionHandler(value = DecodingException.class)
    public ResponseEntity<Result> handleIllegalDecodingException(DecodingException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("登录信息异常"));
    }

    @ExceptionHandler(value = ExecutionException.class)
    public ResponseEntity<Result> handleExecutionException(ExecutionException e) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new Result("请求超时"));
    }

    @ExceptionHandler(value = AsyncRequestTimeoutException.class)
    public ResponseEntity<Result> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e, HttpServletRequest request) {
        System.out.println(request.getRequestURI());
        System.out.println("请求超时");
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new Result("请求超时"));
    }

    @ExceptionHandler(value = TimeoutException.class)
    public ResponseEntity<Result> handleTimeoutException(TimeoutException e, HttpServletRequest request) {
        System.out.println(request.getRequestURI());
        System.out.println("请求超时");
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new Result("请求超时"));
    }
    /*@ExceptionHandler(value = MessagingException.class)
    public ResponseEntity<Result> handleIllegalMessagingException(MessagingException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("登录信息异常"));
    }*/
    /*    @ExceptionHandler(Exception.class)//可以用来找异常类
        public ResponseEntity handleException(Exception ae) {
            System.out.println(ae);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result<>("登录异常"));
            // return null;
        }*/

}
