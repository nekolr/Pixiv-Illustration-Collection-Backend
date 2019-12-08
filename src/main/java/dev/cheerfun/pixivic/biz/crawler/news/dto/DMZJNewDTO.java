package dev.cheerfun.pixivic.biz.crawler.news.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import dev.cheerfun.pixivic.common.po.ACGNew;
import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;

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
    private long createTime;
    @JsonSetter("row_pic_url")
    private String cover;
    @JsonSetter("page_url")
    private String refererUrl;
    @JsonSetter("nickname")
    private String author;
    public ACGNew cast(){
       return new ACGNew(title, intro, author, cover, refererUrl, Instant.ofEpochSecond(createTime).atZone(ZoneId.systemDefault()).toLocalDate(),"动漫之家");
    }
}
