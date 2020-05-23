package dev.cheerfun.pixivic.biz.ad.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.cheerfun.pixivic.biz.ad.po.AdvertisementInfo;
import dev.cheerfun.pixivic.biz.userInfo.dto.ArtistPreViewWithFollowedInfo;
import dev.cheerfun.pixivic.biz.userInfo.dto.IllustrationWithLikeInfo;
import dev.cheerfun.pixivic.common.po.illust.ImageUrl;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/20 下午4:26
 * @description Advertisement
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Advertisement extends IllustrationWithLikeInfo {
    private String link;
    private Integer adId;

    public Advertisement(AdvertisementInfo advertisementInfo) {
        title = advertisementInfo.getTitle();
        imageUrls = new ArrayList<>(1);
        pageCount = 1;
        ImageUrl imageUrl = new ImageUrl();
        String adImageUrl = advertisementInfo.getImageUrl();
        imageUrl.setLarge(adImageUrl);
        imageUrl.setMedium(adImageUrl);
        imageUrl.setOriginal(adImageUrl);
        imageUrl.setSquareMedium(adImageUrl);
        imageUrls.add(imageUrl);
        type = advertisementInfo.getType();
        height = advertisementInfo.getImageHeight();
        width = advertisementInfo.getImageWidth();
        artistPreView = new ArtistPreViewWithFollowedInfo(null, advertisementInfo.getAdvertiserName(), null, advertisementInfo.getAdvertiserAvatar(), false);
        link = advertisementInfo.getLink();
        adId = advertisementInfo.getId();
        sanityLevel = 0;
        restrict = 0;
        xRestrict = 0;
        isLiked = false;

    }

    @Override
    public Integer getId() {
        return Math.toIntExact(System.currentTimeMillis() / 1000);
    }
}
