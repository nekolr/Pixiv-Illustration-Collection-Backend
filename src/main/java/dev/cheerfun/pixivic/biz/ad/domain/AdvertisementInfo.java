package dev.cheerfun.pixivic.biz.ad.domain;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/20 下午10:58
 * @description AdvertisementInfo
 */
@Data
public class AdvertisementInfo {
    private Integer id;
    private String title;
    private String type;
    private String imageUrl;
    private Integer imageHeight;
    private Integer imageWidth;
    private String advertiserName;
    private String advertiserAvatar;
    private String link;
    private Integer weight;
    private Integer useFlag;

}
