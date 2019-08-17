package dev.cheerfun.pixivic.web.user.dto;

import dev.cheerfun.pixivic.web.user.model.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 0:09
 * @description UserSignUpDTO
 */
@Data
public class UserSignUpDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    public User castToUser() {
        return new User(username, email, password);
    }
}
