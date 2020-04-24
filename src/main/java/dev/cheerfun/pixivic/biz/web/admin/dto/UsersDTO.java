package dev.cheerfun.pixivic.biz.web.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/24 3:05 下午
 * @description QueryUsersDTO
 */
@Data
public class UsersDTO {
    private Integer id;
    private String username;
    private String email;
    private Integer gender;
    private Integer permissionLevel;
    private Integer isBan;
    private LocalDateTime createDateStart;
    private LocalDateTime createDateEnd;
    private LocalDateTime updateDateStart;
    private LocalDateTime updateDateEnd;
}
