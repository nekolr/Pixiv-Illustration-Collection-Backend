package dev.cheerfun.pixivic.biz.web.search.domain.response;

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
    @JsonSetter("translation")
    private List<String> result;
}
