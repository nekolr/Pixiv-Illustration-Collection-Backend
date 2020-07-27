package dev.cheerfun.pixivic.common.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private static final String WEB_PRE_PATH = "https://pic.cheerfun.dev/";
    private String uuid;
    private Integer uploadFrom;
    private String original;
    private String large;
    private String medium;
    private String squareMedium;

    public Picture(String uuid, Integer uploadFrom) {
        this.uuid = uuid;
        this.uploadFrom = uploadFrom;
        this.original = WEB_PRE_PATH + uuid + ".jpg";
        this.large = WEB_PRE_PATH + uuid + "_900.jpg";
        this.medium = WEB_PRE_PATH + uuid + "_500.jpg";
        this.squareMedium = WEB_PRE_PATH + uuid + "_500_s.jpg";
    }

}
