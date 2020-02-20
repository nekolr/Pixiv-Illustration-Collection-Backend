package dev.cheerfun.pixivic.basic.userInfo.dto;

import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 20-1-1 下午4:06
 * @description IllustrationWithLikeInfo
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IllustrationWithLikeInfo extends Illustration {
    protected Boolean isLiked;

    public IllustrationWithLikeInfo(Illustration illustration, Boolean isLiked) {
        this.id = illustration.getId();
        this.artistId = illustration.getArtistId();
        this.title = illustration.getTitle();
        this.type = illustration.getType();
        this.caption = illustration.getCaption();
        this.artistPreView = illustration.getArtistPreView();
        this.tags = illustration.getTags();
        this.imageUrls = illustration.getImageUrls();
        this.tools = illustration.getTools();
        this.createDate = illustration.getCreateDate();
        this.pageCount = illustration.getPageCount();
        this.width = illustration.getWidth();
        this.height = illustration.getHeight();
        this.sanityLevel = illustration.getSanityLevel();
        this.restrict = illustration.getRestrict();
        this.xRestrict = illustration.getXRestrict();
        this.totalView = illustration.getTotalView();
        this.totalBookmarks = illustration.getTotalBookmarks();
        this.isLiked = isLiked;
    }

}
