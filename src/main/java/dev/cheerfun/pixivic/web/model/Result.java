package dev.cheerfun.pixivic.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.cheerfun.pixivic.web.constant.StatusCode;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Result<T> {

    private StatusCode statusCode;
    private T data;

}


