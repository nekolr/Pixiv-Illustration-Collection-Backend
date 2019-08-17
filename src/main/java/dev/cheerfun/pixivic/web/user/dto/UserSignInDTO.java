package dev.cheerfun.pixivic.web.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 12:36
 * @description UserSignInDTO
 */
@Data
public class UserSignInDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
