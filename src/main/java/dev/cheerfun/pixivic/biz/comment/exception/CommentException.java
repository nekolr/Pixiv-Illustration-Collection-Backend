package dev.cheerfun.pixivic.biz.comment.exception;

import dev.cheerfun.pixivic.common.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-28 下午5:48
 * @description CommentException
 */
@AllArgsConstructor
@Data
public class CommentException extends BaseException {
    private HttpStatus httpStatus;
    private String message;
}