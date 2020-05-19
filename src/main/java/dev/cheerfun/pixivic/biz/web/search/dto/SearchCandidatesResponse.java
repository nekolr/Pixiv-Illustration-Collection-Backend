package dev.cheerfun.pixivic.biz.web.search.dto;

import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/5/19 10:01 下午
 * @description SearchCandidatesResponse
 */
@Data
public class SearchCandidatesResponse {
    private List<String> keywordList;
}
