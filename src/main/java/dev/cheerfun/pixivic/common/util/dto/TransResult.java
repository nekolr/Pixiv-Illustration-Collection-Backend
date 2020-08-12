package dev.cheerfun.pixivic.common.util.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/11 2:01 下午
 * @description TransResult
 */
@Data
public class TransResult {
    @JsonAlias({"src"})
    private String src;
    @JsonAlias({"dst", "text"})
    private String dst;
}
