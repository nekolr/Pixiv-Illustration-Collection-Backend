package dev.cheerfun.pixivic.common.model;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/14 15:09
 * @description 用户实体
 */
@Data
public class User {
    private int uid;
    private String username;
    private String email;
    private String password;
    private String avatar;
    private int level;

}
