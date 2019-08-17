package dev.cheerfun.pixivic.web.user.controller;

import dev.cheerfun.pixivic.common.model.Result;
import dev.cheerfun.pixivic.verification.annotation.CheckVerification;
import dev.cheerfun.pixivic.web.user.dto.UserSignInDTO;
import dev.cheerfun.pixivic.web.user.dto.UserSignUpDTO;
import dev.cheerfun.pixivic.web.user.model.User;
import dev.cheerfun.pixivic.web.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.constraints.NotBlank;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 0:03
 * @description UserController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/usernames/{username}")
    public ResponseEntity<Result> checkUsername(@NotBlank @PathVariable("username") String username) {
        if (userService.checkUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Result<>("用户名已存在"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result<>("用户名不存在"));
    }

    @GetMapping("/emails/{email:.+}")
    public ResponseEntity<Result<Boolean>> checkEmail(@NotBlank @PathVariable("email") String email) {
        if (userService.checkEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Result<>("邮箱已存在"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result<>("邮箱不存在"));
    }

    @PostMapping
    @CheckVerification
    public ResponseEntity<Result<String>> signUp(@RequestBody UserSignUpDTO userSignUpDTO, @RequestParam("vid") String vid, @RequestParam("value") String value) throws MessagingException {
        User user = userSignUpDTO.castToUser();
        return ResponseEntity.ok().body(new Result<>("注册成功", userService.signUp(user)));
    }

    @PostMapping("/token")
    public ResponseEntity<Result<User>> signIn(@RequestBody UserSignInDTO userSignInDTO) {
        return ResponseEntity.ok().body(new Result<>("登录成功", userService.signIn(userSignInDTO.getUsername(), userSignInDTO.getPassword())));
    }

    @PostMapping("/tokenWithQQ")
    public ResponseEntity<Result<User>> signInByQQ(@RequestBody String qqAccessToken) {
        return ResponseEntity.ok().body(new Result<>("登录成功", userService.signIn(qqAccessToken)));
    }

    @PutMapping("/{userId}/qqAccessToken")
    public ResponseEntity<Result> bindQQ(@RequestBody String qqAccessToken, @PathVariable("userId") String userId, @RequestHeader("Authorization") String token) {
        userService.bindQQ(qqAccessToken, userId, token);
        return ResponseEntity.ok().body(new Result<>("绑定QQ成功"));
    }

    @PutMapping("/{userId}/avatar")
    public ResponseEntity<Result> setAvatar(@RequestBody String avatar, @PathVariable("userId") String userId, @RequestHeader("Authorization") String token) {
        userService.setAvatar(avatar, userId, token);
        return ResponseEntity.ok().body(new Result<>("绑定QQ成功"));
    }

    @PutMapping("/{userId}/email")
    @CheckVerification
    public ResponseEntity<Result> checkEmail(@RequestBody String email, @PathVariable("userId") String userId,  @RequestParam("vid") String vid, @RequestParam("value") String value) {
        userService.setEmail(email,userId);
        return ResponseEntity.ok().body(new Result<>("完成验证邮箱"));
    }

    @PutMapping("/{userId}/password")
    @CheckVerification
    public ResponseEntity<Result> setPassword(@RequestBody String password, @PathVariable("userId") String userId, @RequestHeader("Authorization") String token, @RequestParam("vid") String vid, @RequestParam("value") String value) {
        userService.setPassword(password,userId);
        return ResponseEntity.ok().body(new Result<>("修改密码成功"));
    }
    @GetMapping("/emails/{email:.+}/resetPasswordEmail")
    public ResponseEntity<Result> getResetPasswordEmail(@PathVariable("email") String email) throws MessagingException {
        userService.getResetPasswordEmail(email);
        return ResponseEntity.ok().body(new Result<>("发送密码重置邮件成功"));
    }

}
