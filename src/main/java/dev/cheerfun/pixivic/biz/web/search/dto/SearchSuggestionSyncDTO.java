package dev.cheerfun.pixivic.biz.web.search.dto;

import dev.cheerfun.pixivic.common.model.illust.Tag;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/11/09 14:31
 * @description SearchSuggestionSyncDTO
 */
@Data
public class SearchSuggestionSyncDTO {
    private String keyword;
    private Tag searchSuggestion;
}
