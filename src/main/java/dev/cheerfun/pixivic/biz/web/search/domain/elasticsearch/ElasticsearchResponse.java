package dev.cheerfun.pixivic.biz.web.search.domain.elasticsearch;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/01 21:47
 * @description Response
 */
@Data
public class ElasticsearchResponse {
    private Hits hits;
}
