package dev.cheerfun.pixivic.common.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/27 5:38 下午
 * @description Picture
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Picture {
    private static final String WEB_PRE_PATH = "https://static.sharemoe.net/avatar/299x299";
    private String uuid;
    private Integer uploadFrom;
    private String original;
    private String large;
    private String medium;
    private String squareMedium;
    private String moduleName;
    private LocalDateTime createTime;

    public Picture(String uuid, Integer uploadFrom, String moduleName) {
        this.uuid = uuid;
        this.uploadFrom = uploadFrom;
        this.moduleName = moduleName;
        this.original = WEB_PRE_PATH + moduleName + "/" + uuid + ".jpg";
        this.large = WEB_PRE_PATH + moduleName + "/" + uuid + "_900.jpg";
        this.medium = WEB_PRE_PATH + moduleName + "/" + uuid + "_500.jpg";
        this.squareMedium = WEB_PRE_PATH + moduleName + "/" + uuid + "_500_s.jpg";
    }

}
