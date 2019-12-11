package dev.cheerfun.pixivic.biz.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String commentAppType;
    private Integer commentAppId;
    private Integer commentId;

    @Override
    public String toString() {
        return commentAppType + ':' + commentAppId + ":" + commentId;
    }
}
