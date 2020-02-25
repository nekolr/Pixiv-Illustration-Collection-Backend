package dev.cheerfun.pixivic.biz.crawler.pixiv.dto;

import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/25 上午11:22
 * @description RankDTO
 */
@Data
@AllArgsConstructor
public class RankDTO {
    private List<Illustration> illustrationList;
    private Integer index;
}
