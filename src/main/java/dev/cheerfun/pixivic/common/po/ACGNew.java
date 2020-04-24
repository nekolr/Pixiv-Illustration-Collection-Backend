package dev.cheerfun.pixivic.common.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.cheerfun.pixivic.common.util.json.LocalDateSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/10/12 10:04
 * @description ACGNew
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ACGNew {
    private int id;
    private String title;
    private String intro;
    private String author;
    private String cover;
    private String refererUrl;
    private String content;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate createDate;
    private String referer;

    public ACGNew(String title, String intro, String author, String cover, String refererUrl, String content, LocalDate createDate, String referer) {
        this.title = title;
        this.intro = intro;
        this.author = author;
        this.cover = cover;
        this.refererUrl = refererUrl;
        this.content = content;
        this.createDate = createDate;
        this.referer = referer;
    }

    public ACGNew(String title, String intro, String author, String cover, String refererUrl, LocalDate createDate, String referer) {
        this.title = title;
        this.intro = intro;
        this.author = author;
        this.cover = cover;
        this.refererUrl = refererUrl;
        this.createDate = createDate;
        this.referer = referer;
    }
}
