package dev.cheerfun.pixivic.biz.web.user.dto;

import dev.cheerfun.pixivic.biz.userInfo.dto.ArtistWithIsFollowedInfo;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/15 12:12 上午
 * @description AtristWithRecentlyIllusts
 */
@Data
public class ArtistWithRecentlyIllusts extends ArtistWithIsFollowedInfo {
    private List<Illustration> recentlyIllustrations;

    public ArtistWithRecentlyIllusts(Artist artist, List<Illustration> illustrations) {
        super(artist);
        if (artist instanceof ArtistWithIsFollowedInfo) {
            Boolean isFollowed = ((ArtistWithIsFollowedInfo) artist).getIsFollowed();
            this.isFollowed = isFollowed;
        } else {
            this.isFollowed = null;
        }
        this.recentlyIllustrations = illustrations;
    }

}
