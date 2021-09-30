package dev.cheerfun.pixivic.biz.web.admin.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/16 23:56
 * @description User
 */
@Data
@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class UserPO {
    @Id
    @Column(name = "user_id")
    private Integer id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private String avatar;
    private Integer gender;
    private String signature;
    private String location;
    @Column(name = "permission_level")
    private Integer permissionLevel;
    @Column(name = "is_ban")
    private Integer isBan;
    private Integer star;
    @JsonIgnore
    @Column(name = "pixiv_account")
    private String pixivAccount;
    @JsonIgnore
    @Column(name = "pixiv_password")
    private String pixivPassword;
    @JsonIgnore
    @Column(name = "qq_open_id")
    private String qqOpenId;
    @Column(name = "is_check_email")
    private Integer isCheckEmail;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    public UserPO(String username, String email, String password) {
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

}
