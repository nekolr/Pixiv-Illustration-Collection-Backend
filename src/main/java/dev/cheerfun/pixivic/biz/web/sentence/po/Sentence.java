package dev.cheerfun.pixivic.biz.web.sentence.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/30 9:23 PM
 * @description Sentence
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sentence {
    private Integer id;
    private String content;
    private String originateFrom;
    private String originateFromJP;
    private String author;
}
