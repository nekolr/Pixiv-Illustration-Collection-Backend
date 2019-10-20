package dev.cheerfun.pixivic.crawler.news.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import dev.cheerfun.pixivic.common.model.ACGNew;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/10/12 10:52
 * @description DMZJNewsDTO
 */
@Data
public class DMZJNewDTO {
    private String title;
    private String intro;
    @JsonSetter("create_time")
    private int createTime;
    @JsonSetter("row_pic_url")
    private String cover;
    @JsonSetter("page_url")
    private String refererUrl;
    @JsonSetter("nickname")
    private String author;
    public ACGNew cast(){
       return new ACGNew(title, intro, author, cover, refererUrl, Instant.ofEpochMilli(createTime).atZone(ZoneOffset.ofHours(8)).toLocalDate(),"动漫之家");
    }
}
