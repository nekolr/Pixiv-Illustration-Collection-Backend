package dev.cheerfun.pixivic.crawler.news.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
}
