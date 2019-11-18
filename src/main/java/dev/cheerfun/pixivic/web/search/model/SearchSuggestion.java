package dev.cheerfun.pixivic.web.search.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/14 15:15
 * @description SearchSuggestion
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchSuggestion {
    private String keyword;
    private String keywordTranslated;

    public SearchSuggestion(String keyword) {
        this.keyword = keyword;
    }
}
