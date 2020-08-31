package dev.cheerfun.pixivic.biz.web.admin.po;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/31 4:36 下午
 * @description AdvertisementPO
 */
@Data
@Entity(name = "advertisement")
public class AdvertisementPO {
    @Id
    @Column(name = "ad_id")
    private Integer id;
    private String adTitle;
    private String adType;
    private String link;
    @Column(name = "advertiser_name")
    private String advertiserName;
    @Column(name = "advertiser_avatar")
    private String advertiserAvatar;
    @Column(name = "image_url")
    private Integer imageUrl;
    @Column(name = "image_width")
    private Integer imageWidth;
    @Column(name = "image_height")
    private Integer imageHeight;
    private Integer weight;
    @Column(name = "use_flag")
    private Integer useFlag;
}
