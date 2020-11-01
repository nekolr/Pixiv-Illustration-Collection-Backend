package dev.cheerfun.pixivic.biz.web.sentence.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/30 9:23 PM
 * @description Sentence
 */
@Data
public class Sentence {
    @JsonIgnore
    private Integer id;
    @JsonSetter("hitokoto")
    private String content;
    private String from;
    @JsonSetter("from_who")
    private String fromWho;
}
