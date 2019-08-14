package dev.cheerfun.pixivic.common.exception;

import dev.cheerfun.pixivic.web_common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 14:17
 * @description 全局异常捕获
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 自定义异常捕获，捕获hibernate-validate自定义校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handlerValidMessage(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        assert fieldError != null;
        String defaultMessage = fieldError.getDefaultMessage();
        //使用@NotNull注解异常信息
        log.error("global.exception.handler.error:{}", defaultMessage);
       // return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), defaultMessage);
        return null;
    }

    /**
     * 处理业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<Result> handleBizException(BaseException e) {
        log.error("biz.exception.error:{}", e.getStatusCode().getMsg());
       // return new Result<>(e.getCode(), e.getMsg());
        return null;
    }
}
