package dev.cheerfun.pixivic.biz.web.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/26 2:00 PM
 * @description UserInfoDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Integer id;
    private String username;
    private String avatar;
    private Integer gender;
    private String signature;
    private String location;
    private Integer permissionLevel;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime permissionLevelExpireDate;

    public static UserInfoDTO castByUser(User user) {
        return new UserInfoDTO(user.getId(), user.getUsername(), user.getAvatar(), user.getGender(), user.getSignature(), user.getLocation(), user.getPermissionLevel(), user.getPermissionLevelExpireDate());
    }
}
