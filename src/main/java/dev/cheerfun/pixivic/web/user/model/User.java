package dev.cheerfun.pixivic.web.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.cheerfun.pixivic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.web.user.util.PasswordUtil;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/16 23:56
 * @description User
 */
@Data
public class User {
    @JsonIgnore
    private int userId;
    private String username;
    private String email;
    private String password;
    private String avatar;
    @JsonIgnore
    private int permissionLevel;
    @JsonIgnore
    private int isBan;
    @JsonIgnore
    private String pixivAccount;
    @JsonIgnore
    private String pixivPassword;
    @JsonIgnore
    private String qqAccessToken;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void init() {
        isBan=1;
        password=PasswordUtil.generateSecurePassword(password);
        permissionLevel= PermissionLevel.LOGGED;
        avatar=null;
        pixivAccount=null;
        pixivPassword=null;
        qqAccessToken=null;
    }
}
