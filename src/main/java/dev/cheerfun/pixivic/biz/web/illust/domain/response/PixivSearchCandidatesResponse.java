package dev.cheerfun.pixivic.biz.web.illust.domain.response;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias({"search_auto_complete_keywords", "keywordList"})
    private List<String> keywordList;
}
