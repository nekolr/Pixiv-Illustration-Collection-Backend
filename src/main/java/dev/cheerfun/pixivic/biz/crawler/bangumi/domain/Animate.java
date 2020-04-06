package dev.cheerfun.pixivic.biz.crawler.bangumi.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/4 6:34 下午
 * @description Animate
 */
@Data
public class Animate {
    private Integer id;
    private String title;
    private String translatedTitle;
    private String type;
    private Map<String, String> detail;
    private String intro;
    private List<String> tags;
    private String cover;
    private Float rate;
    private List<AnimateCharacter> characters;
}
