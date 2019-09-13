package dev.cheerfun.pixivic.web.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.cheerfun.pixivic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.auth.model.Authable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public class User implements Authable {
    private int userId;
    private String username;
    private String email;
    @JsonIgnore
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
        star=0;
        isBan=1;
        permissionLevel= PermissionLevel.LOGGED;
        avatar=null;
        pixivAccount=null;
        pixivPassword=null;
        qqAccessToken=null;
    }

    @Override
    @JsonIgnore
    public String getIssuer() {
        return username;
    }

    @Override
    @JsonIgnore
    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("permissionLevel", permissionLevel);
        claims.put("isBan", isBan);
        claims.put("refreshCount", 0);
        claims.put("userId", userId);
        return claims;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return isBan==0;
    }

}
