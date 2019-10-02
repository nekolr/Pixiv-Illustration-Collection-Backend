package dev.cheerfun.pixivic.track.model;

import lombok.Data;

import java.util.Date;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/16 9:41
 * @description Track
 */
@Data
public class Track {
    private Date timestamp;
    private String ip;
    private String url;
    private String method;
    private String agent;
    private String authorization;
}
