package dev.cheerfun.pixivic.web.search.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/14 15:15
 * @description SearchSuggestion
 */
@Data
@AllArgsConstructor
public class SearchSuggestion {
    private String keyword;
    private String keywordTranslated;
}
