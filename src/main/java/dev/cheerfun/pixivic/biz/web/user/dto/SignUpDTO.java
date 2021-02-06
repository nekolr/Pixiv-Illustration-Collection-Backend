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
    @NotBlank(message = "用户名不能为空")
    @SensitiveCheck
    @Length(min = 2, max = 40, message = "用户名长度应该在2-40之间")
    private String username;
    @NotBlank
    @Email(message = "不符合邮箱格式")
    private String email;
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 25, message = "密码长度应该在6-25之间")
    private String password;
    private String gRecaptchaResponse;
    private String exchangeCode;

    public User castToUser() {
        return new User(username, email, password);
    }
}
