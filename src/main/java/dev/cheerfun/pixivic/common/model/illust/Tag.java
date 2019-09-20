package dev.cheerfun.pixivic.common.model.illust;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/11 1:04
 * @description Tag
 */
@Data
@NoArgsConstructor
public class Tag {
    private Long id;
    private String name;
    private String translatedName;

    public Tag(String name, String translatedName) {
        this.name = name;
        this.translatedName = translatedName;
    }
}