package dev.cheerfun.pixivic.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/10/12 10:04
 * @description ACGNew
 */
@Data
@NoArgsConstructor
public class ACGNew {
    private int id;
    private String title;
    private String intro;
    private String author;
    private String cover;
    private String refererUrl;
    private String content;
    private LocalDate createDate;
    private String from;

    public ACGNew(String title, String intro, String author, String cover, String refererUrl, String content, LocalDate createDate, String from) {
        this.title = title;
        this.intro = intro;
        this.author = author;
        this.cover = cover;
        this.refererUrl = refererUrl;
        this.content = content;
        this.createDate = createDate;
        this.from = from;
    }

    public ACGNew(String title, String intro, String author, String cover, String refererUrl, LocalDate createDate, String from) {
        this.title = title;
        this.intro = intro;
        this.author = author;
        this.cover = cover;
        this.refererUrl = refererUrl;
        this.createDate = createDate;
        this.from = from;
    }
}
