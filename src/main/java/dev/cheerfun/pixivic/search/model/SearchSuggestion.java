package dev.cheerfun.pixivic.search.model;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/14 15:15
 * @description SearchSuggestion
 */
@Data
public class SearchSuggestion {
    private String keyword;
    private String keywordTranslated;
}
