package dev.cheerfun.pixivic.biz.web.search.domain.elasticsearch;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/11/28 9:24
 * @description Total
 */
@Data
public class Total {
    private Integer value;
    private String relation;
}
