package dev.cheerfun.pixivic.biz.web.app.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/31 6:22 下午
 * @description AppVersionInfo
 */
@Data
public class AppVersionInfo {
    private Integer id;
    private String version;
    private LocalDateTime releaseDate;
    private String updateLog;
    private String androidLink;
    private String iosLink;
    private Integer isTest;
}
