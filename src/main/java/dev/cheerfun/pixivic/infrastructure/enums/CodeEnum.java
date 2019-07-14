package dev.cheerfun.pixivic.infrastructure.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 14:34
 * @description 响应信息
 */
@AllArgsConstructor
public enum CodeEnum {
    OK("200", "操作成功"),
    PERMISSION_DENIED("401", "权限不足"),
    INTERNAL_SERVER_ERROR("500", "服务器异常"),
    VISIT_OFTEN("403","访问频繁,等一会再来吧!");
    @Getter
    String code;
    @Getter
    String msg;
}
