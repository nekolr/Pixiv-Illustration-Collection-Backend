package dev.cheerfun.pixivic.common.po;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import dev.cheerfun.pixivic.common.util.json.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @JsonSetter("article_url")
    private String articleUrl;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonSetter("publish_date")
    private LocalDate publishDate;
    private String category;
    @JsonSetter("subcategory_label")
    private String subcategoryLabel;

    public void setPublishDate(String publishDate) {
        LocalDateTime parse = LocalDateTime.parse(publishDate, DateTimeFormatter.ISO_INSTANT);
        this.publishDate = parse.toLocalDate();
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }
}
