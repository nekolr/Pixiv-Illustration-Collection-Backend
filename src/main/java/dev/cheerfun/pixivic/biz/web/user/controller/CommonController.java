package dev.cheerfun.pixivic.biz.web.user.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.basic.verification.annotation.CheckVerification;
import dev.cheerfun.pixivic.basic.verification.constant.VerificationType;
import dev.cheerfun.pixivic.biz.web.common.exception.UserCommonException;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.user.dto.*;
import dev.cheerfun.pixivic.biz.web.user.service.CommonService;
import dev.cheerfun.pixivic.biz.web.user.service.VerifService;
import dev.cheerfun.pixivic.biz.web.user.util.PasswordUtil;
import dev.cheerfun.pixivic.biz.web.user.util.RecaptchaUtil;
import dev.cheerfun.pixivic.biz.web.vip.constant.ExchangeCodeBizType;
import dev.cheerfun.pixivic.biz.web.vip.service.ExchangeCodeService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Picture;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

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
    private final ExchangeCodeService exchangeCodeService;
    private final VerifService verifService;
    private final PasswordUtil passwordUtil;
    private final RecaptchaUtil recaptchaUtil;
    private final JWTUtil jwtUtil;

    @GetMapping("/userInfo")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<Object>> querySelfInfo(@RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        User user = userService.queryUser((int) AppContext.get().get(AuthConstant.USER_ID));
        return ResponseEntity.ok().body(new Result<>("????????????????????????", user));
    }

    @GetMapping("/{userId}")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<Object>> queryUser(@PathVariable("userId") Integer userId, @RequestHeader(value = AuthConstant.AUTHORIZATION, required = false) String token) {
        User user = userService.queryUser(userId);
        if (AppContext.get() != null && AppContext.get().get(AuthConstant.USER_ID) != null && (int) AppContext.get().get(AuthConstant.USER_ID) == userId) {
            return ResponseEntity.ok().body(new Result<>("????????????????????????", user));
        }
        return ResponseEntity.ok().body(new Result<>("????????????????????????", UserInfoDTO.castByUser(user)));
    }

    @GetMapping("/usernames/{username}")
    public ResponseEntity<Result> checkUsername(@NotBlank @PathVariable("username") @Length(min = 2, max = 50) String username) {
        if (userService.checkUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Result<>("??????????????????"));
        }
        return ResponseEntity.ok().body(new Result<>("??????????????????"));
    }

    @GetMapping("/emails/{email:.+}")
    public ResponseEntity<Result<Boolean>> checkEmail(@Email @NotBlank @PathVariable("email") String email) {
        if (userService.checkEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Result<>("???????????????"));
        }
        return ResponseEntity.ok().body(new Result<>("???????????????"));
    }

    @GetMapping("/phones/{phone}")
    public ResponseEntity<Result<Boolean>> checkPhone(@NotBlank @PathVariable("phone") String phone) {
        if (userService.checkPhone(phone)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Result<>("??????????????????"));
        }
        return ResponseEntity.ok().body(new Result<>("??????????????????"));
    }

    @PostMapping
    @CheckVerification(VerificationType.MESSAGE)
    public ResponseEntity<Result<User>> signUp(@RequestBody @Valid SignUpDTO userInfo, @RequestParam("vid") String vid, @RequestParam("value") String value) {
        if (userInfo.getUsername().isEmpty()) {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "?????????????????????");
        }
        if (userInfo.getEmail().isEmpty()) {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "??????????????????");
        }
        if (userInfo.getPassword().isEmpty()) {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "??????????????????");
        }
        if (userInfo.getEmail().contains("@") && userInfo.getEmail().contains(".")) {
            if (userInfo.getEmail().lastIndexOf(".") < userInfo.getEmail().lastIndexOf("@")) {
                throw new UserCommonException(HttpStatus.BAD_REQUEST, "??????????????????");
            }
        } else {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "??????????????????");
        }
        if (userInfo.getUsername().length() > 40 || userInfo.getUsername().length() < 2) {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "????????????????????????2-40??????");
        }
        if (userInfo.getPassword().length() > 25 || userInfo.getPassword().length() < 6) {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "?????????????????????6-25??????");
        }
        User user = userInfo.castToUser();
        user.setPhone(vid);
        user = userService.signUp(user, userInfo.getExchangeCode());
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(user)).body(new Result<>("????????????", user));
    }

/*    @PostMapping("/checkByRecaptcha")
    public ResponseEntity<Result<User>> signUpCheckByRecaptcha(@RequestBody SignUpDTO userInfo) throws IOException, InterruptedException {
        recaptchaUtil.check(userInfo.getGRecaptchaResponse());
        User user = userInfo.castToUser();
        user = userService.signUp(user);
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(user)).body(new Result<>("????????????", user));
    }*/

    @PostMapping("/token")
    @CheckVerification
    public ResponseEntity<Result<User>> signIn(@RequestBody SignInDTO userInfo, @RequestParam("vid") String vid, @RequestParam("value") String value) {
        User user = userService.signIn(userInfo.getUsername(), userInfo.getPassword());
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(user)).body(new Result<>("????????????", user));
    }

    @GetMapping("/discussToken")
    @PermissionRequired
    public ResponseEntity<Result<String>> signInDiscuss(@RequestHeader(AuthConstant.AUTHORIZATION) String token) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("????????????token??????", userService.generateDiscussToken((int) AppContext.get().get(AuthConstant.USER_ID))));
    }

    @GetMapping("/tokenWithQQ")
    public ResponseEntity<Result<User>> signInByQQ(@RequestParam String qqAccessToken) throws IOException, InterruptedException {
        User user = userService.signIn(qqAccessToken);
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(user)).body(new Result<>("????????????", user));
    }

    @GetMapping("/tokenWithPhone")
    @CheckVerification
    public ResponseEntity<Result<User>> signInByQQ(@RequestParam("vid") String vid, @RequestParam("value") String value) throws IOException, InterruptedException {
        User user = userService.signInWithPhone(vid);
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(user)).body(new Result<>("????????????", user));
    }

    @PutMapping("/{userId}/qqAccessToken")
    @PermissionRequired
    public ResponseEntity<Result<User>> bindQQ(@RequestParam String qqAccessToken, @PathVariable("userId") int userId, @RequestHeader(AuthConstant.AUTHORIZATION) String token) throws IOException, InterruptedException {
        User user = userService.bindQQ(qqAccessToken, (int) AppContext.get().get(AuthConstant.USER_ID));
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(user)).body(new Result<>("??????QQ??????", user));
    }

    @DeleteMapping("/{userId}/qqAccessToken")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> unbindQQ(@PathVariable("userId") int userId, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("??????QQ??????", userService.unbindQQ((int) AppContext.get().get(AuthConstant.USER_ID))));
    }

    @PutMapping("/{userId}/avatar")
    @PermissionRequired
    public ResponseEntity<Result<String>> setAvatar(@RequestParam String avatar, @PathVariable("userId") int userId, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        userService.setAvatar(avatar, (int) AppContext.get().get(AuthConstant.USER_ID));
        return ResponseEntity.ok().body(new Result<>("??????????????????", avatar));
    }

    @PutMapping("/{userId}/email")
    @PermissionRequired
    public ResponseEntity<Result<User>> checkEmail(@RequestParam @Email String email, @PathVariable("userId") int userId, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        userId = (int) AppContext.get().get(AuthConstant.USER_ID);
        userService.setEmail(email, userId);
        User user = userService.queryUser(userId);
        userService.getCheckEmail(email, userId);
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(user)).body(new Result<>("??????????????????????????????????????????", user));
    }

    @PutMapping("/{userId}/emailCheck")
    @CheckVerification
    public ResponseEntity<Result<User>> checkEmail(@RequestParam @Email String email, @PathVariable("userId") int userId, @RequestParam("vid") String vid, @RequestParam("value") String value) {
        String emailAddr = value.substring(value.indexOf(":") + 1);
        User user = userService.queryUserByEmail(emailAddr);
        userService.checkEmail(emailAddr, user.getId());
        user = userService.queryUser(userId);
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(user)).body(new Result<>("??????????????????", user));
    }

    @PutMapping("/{userId}")
    @PermissionRequired
    public ResponseEntity<Result<User>> updateUserInfo(@PathVariable("userId") Integer userId, @RequestBody User user, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        userService.updateUserInfo((int) AppContext.get().get(AuthConstant.USER_ID), user);
        return ResponseEntity.ok().body(new Result<>("????????????????????????", user));
    }

    //????????????
    @PostMapping("/checkUsername")
    public ResponseEntity<Result<String>> checkUsernameSensitive(@RequestParam String username) {
        return ResponseEntity.ok().body(new Result<>("?????????????????????????????????", userService.checkUsernameSensitive(username)));
    }

    //????????????
    @PutMapping("/{userId}/username")
    @PermissionRequired
    public ResponseEntity<Result<User>> updateUsername(@RequestParam String username, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        User user = null;
        //   try {
        user = userService.updateUsername((Integer) AppContext.get().get(AuthConstant.USER_ID), username);
      /*  } catch (Exception e) {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "???????????????????????????????????????");
        }*/
        return ResponseEntity.ok().body(new Result<>("????????????????????????????????????????????????????????????", user));
    }

    @GetMapping("/{userId}/email/isCheck")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> queryEmailIsCheck(@PathVariable("userId") int userId, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        Boolean isCheck = userService.queryEmailIsCheck((int) AppContext.get().get(AuthConstant.USER_ID));
        return ResponseEntity.ok().body(new Result<>("??????????????????????????????", isCheck));
    }

    @GetMapping("/{userId}/isBindQQ")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> queryIsBindQQ(@PathVariable("userId") int userId, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        Boolean isBind = userService.queryIsBindQQ((int) AppContext.get().get(AuthConstant.USER_ID));
        return ResponseEntity.ok().body(new Result<>("??????QQ??????????????????", isBind));
    }

    @PutMapping("/password")
    @CheckVerification
    public ResponseEntity<Result> resetPassword(@RequestBody ResetPasswordDTO item, @RequestParam("vid") String vid, @RequestParam("value") String value) {
        userService.setPasswordByEmail(item.getPassword(), value.substring(value.indexOf(":") + 1));
        return ResponseEntity.ok().body(new Result<>("??????????????????"));
    }

    @PutMapping("/{userId}/password")
    @PermissionRequired
    public ResponseEntity<Result> setPassword(@RequestHeader(AuthConstant.AUTHORIZATION) String token, @RequestBody ResetPasswordDTO item) {
        int userId = (int) AppContext.get().get(AuthConstant.USER_ID);
        if (userService.validatePassword(userId, item.getOldPassword())) {
            userService.setPasswordById(passwordUtil.encrypt(item.getPassword()), userId);
            return ResponseEntity.ok().body(new Result<>("??????????????????"));
        }
        return ResponseEntity.badRequest().body(new Result<>("??????????????????"));

    }

    @GetMapping("/emails/{email:.+}/resetPasswordEmail")
    public ResponseEntity<Result> getResetPasswordEmail(@PathVariable("email") @Email String email) {
        userService.getResetPasswordEmail(email);
        return ResponseEntity.ok().body(new Result<>("??????????????????????????????"));
    }

    @GetMapping("/emails/{email:.+}/checkEmail")
    @PermissionRequired
    public ResponseEntity<Result> getCheckEmail(@PathVariable("email") @Email String email, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        userService.checkEmail(email);
        userService.getCheckEmail(email, (int) AppContext.get().get(AuthConstant.USER_ID));
        return ResponseEntity.ok().body(new Result<>("??????????????????????????????"));
    }

    /*@PostMapping("/{moduleName}/image")
    @PermissionRequired
    public ResponseEntity<Result<Picture>> uploadModuleImage(@PathVariable("moduleName") String moduleName, @RequestParam("file") MultipartFile file, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("??????????????????", userService.uploadModuleImage(moduleName, file, (int) AppContext.get().get(AuthConstant.USER_ID))));
    }*/

    @PostMapping("/image/uploadLog")
    public ResponseEntity<Result<Boolean>> uploadModuleImageLog(@RequestBody Picture picture) {
        //userService.uploadModuleImageLog(moduleName, (int) AppContext.get().get(AuthConstant.USER_ID));
        return ResponseEntity.ok().body(new Result<>("????????????????????????????????????", userService.uploadModuleImageLog(picture)));
    }

    @PutMapping("/{userId}/permissionLevel")
    @PermissionRequired
    public ResponseEntity<Result<User>> updatePermissionLevel(@RequestParam String exchangeCode, @PathVariable("userId") int userId, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        Integer uid = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        exchangeCodeService.exchangeCode(uid, exchangeCode, ExchangeCodeBizType.VIP);
        User user = userService.queryUser(uid);
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(user)).body(new Result<>("????????????", user));
    }

    @GetMapping("/check-in")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> queryDailyCheckIn(@RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("??????????????????????????????", userService.queryCheckInStatus((Integer) AppContext.get().get(AuthConstant.USER_ID))));
    }

    @PostMapping("/check-in")
    @PermissionRequired
    public ResponseEntity<Result<CheckInDTO>> dailyCheckIn(@RequestHeader(AuthConstant.AUTHORIZATION) String token) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("????????????", userService.dailyCheckIn((Integer) AppContext.get().get(AuthConstant.USER_ID))));
    }

    //??????pixiv??????

    //???????????????
    @PutMapping("/{userId}/phone")
    @PermissionRequired
    @CheckVerification(VerificationType.MESSAGE)
    public ResponseEntity<Result<User>> updatePhone(@PathVariable("userId") Integer userId, @RequestParam("vid") String vid, @RequestParam("value") String value, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        int uid = (int) AppContext.get().get(AuthConstant.USER_ID);
        userService.setPhone(vid, uid);
        User user = userService.queryUser(uid);
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(user)).body(new Result<>("??????????????????????????????", user));
    }

    //????????????
    @PutMapping("/{userId}/verifiedInfo")
    @PermissionRequired
    public ResponseEntity<Result<User>> verified(@RequestBody VerifiedDTO verifiedDTO, @RequestHeader(AuthConstant.AUTHORIZATION) String token) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        int uid = (int) AppContext.get().get(AuthConstant.USER_ID);
        verifService.verified(verifiedDTO, uid);
        User user = userService.queryUser(uid);
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(user)).body(new Result<>("??????????????????", user));
    }

}
