package dev.cheerfun.pixivic.biz.web.user.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.basic.verification.annotation.CheckVerification;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.user.dto.ResetPasswordDTO;
import dev.cheerfun.pixivic.biz.web.user.dto.SignInDTO;
import dev.cheerfun.pixivic.biz.web.user.dto.SignUpDTO;
import dev.cheerfun.pixivic.biz.web.user.service.CommonService;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.IOException;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 0:03
 * @description UserController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/users")
public class CommonController {
    private final CommonService userService;
    private final JWTUtil jwtUtil;
    private static final String USER_ID = "userId";

    @GetMapping("/usernames/{username}")
    public ResponseEntity<Result> checkUsername(@NotBlank @PathVariable("username") @Size(min = 2, max = 50) String username) {
        if (userService.checkUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Result<>("用户名已存在"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result<>("用户名不存在"));
    }

    @GetMapping("/emails/{email:.+}")
    public ResponseEntity<Result<Boolean>> checkEmail(@Email @NotBlank @PathVariable("email") String email) {
        if (userService.checkEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Result<>("邮箱已存在"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result<>("邮箱不存在"));
    }

    @PostMapping
    @CheckVerification
    public ResponseEntity<Result<User>> signUp(@RequestBody SignUpDTO userInfo, @RequestParam("vid") String vid, @RequestParam("value") String value) throws MessagingException {
        User user = userInfo.castToUser();
        user = userService.signUp(user);
        return ResponseEntity.ok().header("Authorization", jwtUtil.getToken(user)).body(new Result<>("注册成功", user));
    }

    @PostMapping("/token")
    @CheckVerification
    public ResponseEntity<Result<User>> signIn(@RequestBody SignInDTO userInfo, @RequestParam("vid") String vid, @RequestParam("value") String value) {
        User user = userService.signIn(userInfo.getUsername(), userInfo.getPassword());
        return ResponseEntity.ok().header("Authorization", jwtUtil.getToken(user)).body(new Result<>("登录成功", user));
    }

    @PostMapping("/tokenWithQQ")
    public ResponseEntity<Result<User>> signInByQQ(@RequestBody String qqAccessToken) throws IOException, InterruptedException {
        User user = userService.signIn(qqAccessToken);
        return ResponseEntity.ok().header("Authorization", jwtUtil.getToken(user)).body(new Result<>("登录成功", user));
    }

    @PutMapping("/{userId}/qqAccessToken")
    @PermissionRequired
    public ResponseEntity<Result> bindQQ(@RequestParam String qqAccessToken, @PathVariable("userId") int userId, @RequestHeader("Authorization") String token) throws IOException, InterruptedException {
        userService.bindQQ(qqAccessToken, (int) AppContext.get().get(USER_ID));
        return ResponseEntity.ok().body(new Result<>("绑定QQ成功"));
    }

    @PutMapping("/{userId}/avatar")
    @PermissionRequired
    public ResponseEntity<Result<String>> setAvatar(@RequestParam String avatar, @PathVariable("userId") int userId, @RequestHeader("Authorization") String token) {
        userService.setAvatar(avatar, (int) AppContext.get().get(USER_ID));
        return ResponseEntity.ok().body(new Result<>("修改头像成功", avatar));
    }

    @PutMapping("/{userId}/email")
    @CheckVerification
    public ResponseEntity<Result<User>> checkEmail(@RequestParam @Email String email, @PathVariable("userId") int userId, @RequestParam("vid") String vid, @RequestParam("value") String value) {
        User user = userService.setEmail(email, userId);
        return ResponseEntity.ok().header("Authorization", jwtUtil.getToken(user)).body(new Result<>("完成重置邮箱", user));
    }

    @GetMapping("/{userId}/email/isCheck")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> queryEmailIsCheck(@PathVariable("userId") int userId, @RequestHeader("Authorization") String token) {
        Boolean isCheck = userService.queryEmailIsCheck((int) AppContext.get().get(USER_ID));
        return ResponseEntity.ok().body(new Result<>("获取邮箱验证状态成功", isCheck));
    }
    @GetMapping("/{userId}/isBindQQ")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> queryIsBindQQ(@PathVariable("userId") int userId, @RequestHeader("Authorization") String token) {
        Boolean isBind = userService.queryIsBindQQ((int) AppContext.get().get(USER_ID));
        return ResponseEntity.ok().body(new Result<>("获取QQ绑定状态成功", isBind));
    }

    @PutMapping("/password")
    @CheckVerification
    public ResponseEntity<Result> resetPassword(@RequestBody ResetPasswordDTO item, @RequestParam("vid") String vid, @RequestParam("value") String value) {
        userService.setPasswordByEmail(item.getPassword(), value.substring(4));
        return ResponseEntity.ok().body(new Result<>("重置密码成功"));
    }

    @PutMapping("/{userId}/password")
    @PermissionRequired
    public ResponseEntity<Result> setPassword(@RequestHeader("Authorization") String token, @RequestBody ResetPasswordDTO item) {
        userService.setPasswordById(item.getPassword(), (int) AppContext.get().get(USER_ID));
        return ResponseEntity.ok().body(new Result<>("修改密码成功"));
    }

    @GetMapping("/emails/{email:.+}/resetPasswordEmail")
    public ResponseEntity<Result> getResetPasswordEmail(@PathVariable("email") @Email String email) throws MessagingException {
        userService.getResetPasswordEmail(email);
        return ResponseEntity.ok().body(new Result<>("发送密码重置邮件成功"));
    }

    @GetMapping("/emails/{email:.+}/checkEmail")
    @PermissionRequired
    public ResponseEntity<Result> getCheckEmail(@PathVariable("email") @Email String email, @RequestHeader("Authorization") String token) throws MessagingException {
        userService.checkEmail(email);
        userService.getCheckEmail(email, (int) AppContext.get().get(USER_ID));
        return ResponseEntity.ok().body(new Result<>("发送邮箱验证邮件成功"));
    }

}
