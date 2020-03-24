package dev.cheerfun.pixivic.biz.analysis.track.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/16 9:41
 * @description Track
 */
@Data
public class Track {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;
    private String ip;
    private String url;
    private String args;
    private String body;
    private String method;
    private String agent;
    private String authorization;
    private int userId;
    private String desc;
}
