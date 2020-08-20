package dev.cheerfun.pixivic.common.util.translate.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/11 1:56 下午
 * @description BaiduTranslatedResponse
 */
@Data
public class BaiduTranslatedResponse {
    @JsonSetter("trans_result")
    private List<TransResult> transResult;
}

