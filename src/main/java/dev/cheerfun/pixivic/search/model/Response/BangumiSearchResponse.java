package dev.cheerfun.pixivic.search.model.Response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/14 15:16
 * @description BangumiSearchResult
 */
@Data
public class BangumiSearchResponse {
    @JsonSetter("list")
    private List<ResultItem> Result;
}
@Data
class ResultItem{
    @JsonSetter("name_cn")
    private String keyword;
    @JsonSetter("name")
    private String keywordTranslated;
}
