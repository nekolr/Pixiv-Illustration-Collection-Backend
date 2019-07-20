package dev.cheerfun.pixivic.web.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/20 9:50
 * @description 状态码
 */
@AllArgsConstructor
public enum StatusCode {
    SUCCESS(100, "请求完成");
    @Getter
    private int code;
    @Getter
    private String msg;
}
