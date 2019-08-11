package dev.cheerfun.pixivic.crawler.dto;

import dev.cheerfun.pixivic.common.model.Artist;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/01 9:32
 * @description ArtistDTO
 */
@Data
public class ArtistDTO {
    private User user;
    private Profile profile;

    public static Artist castToArtist(ArtistDTO artistDTO) {
        Artist artist = new Artist();
        User user = artistDTO.getUser();
        artist.setId(user.getId());
        artist.setName(user.getName());
        artist.setAccount(user.getAccount());
        artist.setAvatar(user.getProfile_image_urls().getMedium());
        artist.setComment(user.getComment());
        Profile profile = artistDTO.getProfile();
        artist.setGender(profile.getGender());
        artist.setBirthDay(profile.getBirth_day());
        artist.setRegion(profile.getRegion());
        artist.setWebPage(profile.getWebpage());
        artist.setTwitterAccount(profile.getTwitter_account());
        artist.setTwitterUrl(profile.getTwitter_url());
        artist.setTotalFollowUsers(profile.getTotal_follow_users());
        artist.setTotalIllustBookmarksPublic(profile.getTotal_illust_bookmarks_public());
        return artist;
    }
}

@Data
class ProfileImageUrls {
    private String medium;
}

@Data
class User {
    private Integer id;
    private String name;
    private String account;
    private ProfileImageUrls profile_image_urls;
    private String comment;
}

@Data
class Profile {
    private String gender;
    private String birth_day;
    private String region;
    private String webpage;
    private String twitter_account;
    private String twitter_url;
    private String total_follow_users;
    private String total_illust_bookmarks_public;
}
