package dev.cheerfun.pixivic.biz.web.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/12/09 20:33
 * @description Like
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Like {
    @NotBlank
    private String commentAppType;
    @NotNull
    private Integer commentAppId;
    @NotNull
    private Integer commentId;

    @Override
    public String toString() {
        return commentAppType + ':' + commentAppId + ":" + commentId;
    }
}
