package dev.cheerfun.pixivic.web.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.cheerfun.pixivic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.auth.model.Authable;
import dev.cheerfun.pixivic.web.user.util.PasswordUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/16 23:56
 * @description User
 */
@Data
@AllArgsConstructor
public class User implements Authable {
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
    private int star;
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

    @Override
    public String getIssuer() {
        return username;
    }

    @Override
    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("permissionLevel", permissionLevel);
        claims.put("isBan", isBan);
        claims.put("refreshCount", 0);
        claims.put("userId", userId);
        return claims;
    }

    @Override
    public boolean isEnabled() {
        return isBan==0;
    }

}
