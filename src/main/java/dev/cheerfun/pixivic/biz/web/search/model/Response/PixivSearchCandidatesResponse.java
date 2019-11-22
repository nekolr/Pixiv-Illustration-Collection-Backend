package dev.cheerfun.pixivic.biz.web.search.model.Response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/14 23:13
 * @description PixivSearchCandidatesResponse
 */
@Data
public class PixivSearchCandidatesResponse {
    @JsonSetter("search_auto_complete_keywords")
    private List<String> keywordList;
}
