package dev.cheerfun.pixivic.common.exception;

import dev.cheerfun.pixivic.common.constant.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 16:39
 * @description 基础异常
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseException extends RuntimeException {
    protected StatusCode statusCode;
    protected HttpStatus httpStatus;

}
