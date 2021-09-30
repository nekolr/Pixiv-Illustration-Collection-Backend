package dev.cheerfun.pixivic.common.po;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import dev.cheerfun.pixivic.common.util.json.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    @JsonAlias({"pure_title", "pureTitle"})
    private String pureTitle;
    private String thumbnail;
    @JsonAlias({"article_url", "articleUrl"})
    private String articleUrl;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonAlias({"publish_date", "publishDate"})
    private LocalDate publishDate;
    private String category;
    @JsonAlias({"subcategory_label", "subcategoryLabel"})
    private String subcategoryLabel;

    public void setPublishDate(String publishDate) {
        LocalDateTime parse = LocalDateTime.parse(publishDate, DateTimeFormatter.ISO_INSTANT);
        this.publishDate = parse.toLocalDate();
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }
}
