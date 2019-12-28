package dev.cheerfun.pixivic.basic.track.domain;

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
