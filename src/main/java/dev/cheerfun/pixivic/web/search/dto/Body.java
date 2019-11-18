package dev.cheerfun.pixivic.web.search.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-11-18 下午10:44
 * @description Body
 */
@Data
public class Body {
    private List<String> relatedTags;
    private Map<String, TagTranslation> tagTranslation;
}
