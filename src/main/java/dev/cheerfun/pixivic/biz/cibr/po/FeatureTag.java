package dev.cheerfun.pixivic.biz.cibr.po;

import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/9/23 5:52 PM
 * @description FeatureTag
 */
@Data
public class FeatureTag {
    private Integer id;
    private String content;
    private Integer type;
    private List<String> pixivTags;
    private String desc;
    private String CNDesc;
    private String extendContent;
}
