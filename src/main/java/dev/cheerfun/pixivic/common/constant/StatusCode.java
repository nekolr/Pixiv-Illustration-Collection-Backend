package dev.cheerfun.pixivic.common.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/20 9:50
 * @description 状态码
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatusCode {
    SUCCESS(100, "请求完成"),
    TOKEN_EXPIRATION_OUT_OF_TIME(201,"token过期"),
    PERMISSION_DENIED(202,"用户权限不足"),
    VERIFICATION_CODE_NOT_MATCH(301,"验证码错误");

    @Getter
    private int code;
    @Getter
    private String msg;
}
