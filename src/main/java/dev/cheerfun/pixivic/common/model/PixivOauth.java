package dev.cheerfun.pixivic.common.model;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/20 11:18
 * @description pixiv账户
 */
@Data
public class PixivOauth {
    private int uid;
    private String username;
    private String password;
}
