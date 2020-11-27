package dev.cheerfun.pixivic.biz.web.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 12:36
 * @description UserSignInDTO
 */
@Data
public class SignInDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
