package dev.cheerfun.pixivic.biz.web.user.po;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/19 9:33
 * @description FollowedRelation
 */
@Data
@NoArgsConstructor
public class FollowedRelation {
    @NonNull
    private Integer userId;
    @NonNull
    private String username;
    @NonNull
    private Integer artistId;

}
