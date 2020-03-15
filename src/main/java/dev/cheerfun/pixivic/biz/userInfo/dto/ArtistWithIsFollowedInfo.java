package dev.cheerfun.pixivic.biz.userInfo.dto;

import dev.cheerfun.pixivic.common.po.Artist;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/1/31 下午8:06
 * @description ArtistWithIsFollowedInfo
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ArtistWithIsFollowedInfo extends Artist {
    protected Boolean isFollowed;

    public ArtistWithIsFollowedInfo(Artist artist, Boolean isFollowed) {
        this.isFollowed = isFollowed;
        this.id = artist.getId();
        this.name = artist.getName();
        this.account = artist.getAccount();
        this.avatar = artist.getAvatar();
        this.comment = artist.getComment();
        this.gender = artist.getGender();
        this.birthDay = artist.getBirthDay();
        this.region = artist.getRegion();
        this.webPage = artist.getWebPage();
        this.twitterAccount = artist.getTwitterAccount();
        this.twitterUrl = artist.getTwitterUrl();
        this.totalFollowUsers = artist.getTotalFollowUsers();
        this.totalIllustBookmarksPublic = artist.getTotalIllustBookmarksPublic();
    }
}
