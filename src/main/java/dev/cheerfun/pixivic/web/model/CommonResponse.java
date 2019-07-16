package dev.cheerfun.pixivic.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 14:18
 * @description 公共响应类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {

    private String code;
    private String msg;
    private T data;

    public CommonResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
