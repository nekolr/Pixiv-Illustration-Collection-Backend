package dev.cheerfun.pixivic.common.util.dto;

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
public class AzureTranslatedResponse {
    @JsonSetter("translations")
    private List<TransResult> transResult;
}

