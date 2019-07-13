package dev.cheerfun.pixivic.web.execption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 14:48
 * @description 自定义业务异常
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BizException extends RuntimeException {
    private String code;
    private String msg;
}
