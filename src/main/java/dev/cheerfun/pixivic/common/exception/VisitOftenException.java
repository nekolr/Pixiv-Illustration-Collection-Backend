package dev.cheerfun.pixivic.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 14:48
 * @description 访问频繁异常
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class VisitOftenException extends BaseException {
    public VisitOftenException(int code, String msg) {
        super(code, msg);
    }
}
