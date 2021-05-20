package dev.cheerfun.pixivic.biz.web.user.dto;

import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 0:09
 * @description UserSignUpDTO
 */
@Data
public class SignUpDTO {
    @SensitiveCheck
    private String username;
    private String email;
    private String password;
    private String gRecaptchaResponse;
    private String exchangeCode;

    public User castToUser() {
        return new User(username, email, password);
    }
}
