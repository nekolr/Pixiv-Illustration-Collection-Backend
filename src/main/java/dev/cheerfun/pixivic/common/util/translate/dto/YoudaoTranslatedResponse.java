package dev.cheerfun.pixivic.common.util.translate.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/14 21:53
 * @description YoudaoTranslatedResponse
 */
@Data
public class YoudaoTranslatedResponse {
    @JsonAlias({"translation", "result"})
    private List<String> result;
}
