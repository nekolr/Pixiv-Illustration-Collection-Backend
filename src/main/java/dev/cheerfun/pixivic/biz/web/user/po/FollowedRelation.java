package dev.cheerfun.pixivic.biz.web.user.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/19 9:33
 * @description FollowedRelation
 */
@Data
@NoArgsConstructor
public class FollowedRelation {
    @NotNull
    private Integer userId;
    @NotNull
    private String username;
    @NotNull
    private Integer artistId;

}
