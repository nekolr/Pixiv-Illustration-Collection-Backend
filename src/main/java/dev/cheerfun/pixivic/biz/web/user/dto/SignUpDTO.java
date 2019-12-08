package dev.cheerfun.pixivic.biz.web.user.dto;

import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 0:09
 * @description UserSignUpDTO
 */
@Data
public class SignUpDTO {
    @NotBlank
    @SensitiveCheck
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private SignUpDTO signUpDTO;

    public User castToUser() {
        return new User(username, email, password);
    }
}
