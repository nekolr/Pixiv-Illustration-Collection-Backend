package dev.cheerfun.pixivic.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.Date;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/27 17:29
 * @description Spotlight
 */
@Data
public class Spotlight {
    private int id;
    private String title;
    @JsonSetter("pure_title")
    private String pureTitle;
    private String thumbnail;
    @JsonIgnore
    @JsonSetter("article_url")
    private String articleUrl;
    @JsonSetter("publish_date")
    private Date publishDate;
    private String category;
    @JsonSetter("subcategory_label")
    private String subcategoryLabel;
}
