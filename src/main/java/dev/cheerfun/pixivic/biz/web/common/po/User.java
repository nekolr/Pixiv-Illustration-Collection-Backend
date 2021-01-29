package dev.cheerfun.pixivic.biz.web.common.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.auth.domain.Authable;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private Integer id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private String avatar;
    private Integer gender;
    private String signature;
    private String location;
    private Integer permissionLevel;
    private Integer isBan;
    private Integer star;
    @JsonIgnore
    private String pixivAccount;
    @JsonIgnore
    private String pixivPassword;
    @JsonIgnore
    private String qqOpenId;
    private Integer isCheckEmail;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createDate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updateDate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime permissionLevelExpireDate;
    private String phone;
    @JsonIgnore
    private String idCard;
    @JsonIgnore
    private String addrForVerify;
    @JsonIgnore
    private String birthdayForVerify;
    private String ageForVerify;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void init() {
        star = 0;
        isBan = 1;
        permissionLevel = PermissionLevel.LOGGED;
        avatar = null;
        pixivAccount = null;
        pixivPassword = null;
        qqOpenId = null;
        gender = null;
        signature = null;
        location = null;
        isCheckEmail = 0;
        createDate = LocalDateTime.now();
        updateDate = null;
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
        claims.put(AuthConstant.PERMISSION_LEVEL, permissionLevel);
        //claims.put(AuthConstant.IS_BAN, isBan);
        claims.put(AuthConstant.IS_CHECK_PHONE, getIsCheckPhone());
        claims.put(AuthConstant.REFRESH_COUNT, 0);
        claims.put(AuthConstant.USER_ID, id);
        return claims;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return isBan == 0;
    }

    public boolean getIsBindQQ() {
        return qqOpenId != null;
    }

    public boolean getIsCheckEmail() {
        return isCheckEmail != null && isCheckEmail == 1;
    }

    public int getIsCheckPhone() {
        return phone == null ? 0 : 1;
    }

}
