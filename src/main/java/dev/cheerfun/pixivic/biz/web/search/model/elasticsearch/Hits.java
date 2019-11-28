package dev.cheerfun.pixivic.biz.web.search.model.elasticsearch;

import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/09 23:33
 * @description Hits
 */
@Data
public class Hits {
    private List<Hit> hits;
    private Total total;
}
