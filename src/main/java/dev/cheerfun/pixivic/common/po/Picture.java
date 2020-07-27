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
    private String uuid;
    private Integer uploadFrom;
    private Integer original;
    private Integer large;
    private Integer medium;
    private Integer squareMedium;

    public Picture(String uuid, Integer uploadFrom) {
        this.uuid = uuid;
        this.uploadFrom = uploadFrom;
    }
}
