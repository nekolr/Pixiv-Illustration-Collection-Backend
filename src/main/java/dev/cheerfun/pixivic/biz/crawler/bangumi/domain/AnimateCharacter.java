package dev.cheerfun.pixivic.biz.crawler.bangumi.domain;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/4 6:38 下午
 * @description Character
 */
@Data
public class AnimateCharacter {
    private Integer id;
    private String avatar;
    private String name;
    private String translatedName;
    private String detail;
    private String type;
    private Seiyuu seiyuu;

}
