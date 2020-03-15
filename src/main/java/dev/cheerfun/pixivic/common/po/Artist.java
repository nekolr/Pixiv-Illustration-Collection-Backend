package dev.cheerfun.pixivic.common.po;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/31 9:29
 * @description Artist
 */
@Data
public class Artist {
    protected Integer id;
    protected String name;
    protected String account;
    protected String avatar;
    protected String comment;
    protected String gender;
    protected String birthDay;
    protected String region;
    protected String webPage;
    protected String twitterAccount;
    protected String twitterUrl;
    protected String totalFollowUsers;
    protected String totalIllustBookmarksPublic;

}
