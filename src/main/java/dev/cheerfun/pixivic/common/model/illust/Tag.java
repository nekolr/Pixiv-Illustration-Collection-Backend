package dev.cheerfun.pixivic.common.model.illust;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/11 1:04
 * @description Tag
 */
@Data
public class Tag {
    @JsonIgnore
    private long id;
    private String name;
    private String translatedName;
}