package dev.cheerfun.pixivic.crawler.news.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.cheerfun.pixivic.common.model.ACGNew;
import lombok.Data;

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
    @JsonProperty("create_time")
    private int createTime;
    @JsonProperty("row_pic_url")
    private String cover;
    @JsonProperty("page_url")
    private String refererUrl;
    @JsonProperty("nickname")
    private String author;
    public ACGNew cast(){
       return new ACGNew(title, intro, author, cover, refererUrl, LocalDateTime.ofEpochSecond(createTime/ 1000, 0, ZoneOffset.ofHours(8)),"动漫之家");
    }
}
