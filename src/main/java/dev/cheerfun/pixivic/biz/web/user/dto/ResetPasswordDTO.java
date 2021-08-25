package dev.cheerfun.pixivic.biz.web.user.dto;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/10/17 下午9:24
 * @description resetPasswordDTO
 */
@Data
public class ResetPasswordDTO {
    private String password;
    private String oldPassword;
}
