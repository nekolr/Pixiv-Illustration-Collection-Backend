package dev.cheerfun.pixivic.biz.web.admin.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/31 6:48 下午
 * @description AppVersionInfoPO
 */
@Data
@Entity(name = "app_version_info")
public class AppVersionInfoPO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_version_info_id")
    private Integer id;
    private String version;
    @Column(name = "release_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime releaseDate;
    @Column(name = "update_log")
    private String updateLog;
    @Column(name = "android_link")
    private String androidLink;
    @Column(name = "ios_link")
    private String iosLink;
    @Column(name = "is_test")
    private Integer isTest;
}
