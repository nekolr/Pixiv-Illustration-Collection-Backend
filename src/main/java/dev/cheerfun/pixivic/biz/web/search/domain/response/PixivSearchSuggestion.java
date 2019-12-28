package dev.cheerfun.pixivic.biz.web.search.domain.response;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/16 15:44
 * @description PixivSearchSuggestion
 */
@Data
public class PixivSearchSuggestion {
    private String tag;
    private String tag_translation;
}
