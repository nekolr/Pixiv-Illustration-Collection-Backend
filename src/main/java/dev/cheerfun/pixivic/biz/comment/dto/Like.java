package dev.cheerfun.pixivic.biz.comment.dto;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/12/09 20:33
 * @description Like
 */
@Data
public class Like {
    private Integer userId;
    private String commentAppType;
    private Integer commentAppId;
    private Integer commentId;
}
