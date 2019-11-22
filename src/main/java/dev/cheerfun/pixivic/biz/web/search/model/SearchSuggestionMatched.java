package dev.cheerfun.pixivic.biz.web.search.model;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/14 23:24
 * @description SearchSuggestionMatched
 */
@Data
public class SearchSuggestionMatched {
    private String keyword;
    private String tagName;
}
