package dev.cheerfun.pixivic.biz.cibr.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/13 8:36 PM
 * @description IllustFeature
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IllustFeature {
    private Long illustId;
    private Integer imagePage;
    private String feature;
}
