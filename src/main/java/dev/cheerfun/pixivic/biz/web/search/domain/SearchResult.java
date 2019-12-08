package dev.cheerfun.pixivic.biz.web.search.domain;

import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-11-28 下午9:10
 * @description SearchResult
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private int total;
    private List<Illustration> illustrations;
}
