package dev.cheerfun.pixivic.biz.web.user.po;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/5/11 4:11 下午
 * @description FollowedUserRelation
 */
@Data
public class FollowedUserRelation {
    @NotNull
    private Integer userId;
    @NotNull
    private String username;
    @NotNull
    private Integer followedUserId;
}
