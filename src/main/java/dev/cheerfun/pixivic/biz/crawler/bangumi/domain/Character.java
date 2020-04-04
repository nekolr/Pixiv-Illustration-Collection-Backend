package dev.cheerfun.pixivic.biz.crawler.bangumi.domain;

import lombok.Data;

import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/4 6:38 下午
 * @description Character
 */
@Data
public class Character {
    private Integer id;
    private String name;
    private String translatedName;
    private Map<String, String> detail;
    private String type;
    private Seiyuu seiyuu;

}
