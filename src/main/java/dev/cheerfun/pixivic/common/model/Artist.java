package dev.cheerfun.pixivic.common.model;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/31 9:29
 * @description Artist
 */
@Data
public class Artist {
    private Integer id;
    private String name;
    private String account;
    private String avatar;
    private String comment;
    private String gender;
    private String birthDay;
    private String region;
    private String webPage;
    private String twitterAccount;
    private String twitterUrl;
    private String totalFollowUsers;
    private String totalIllustBookmarksPublic;
}
