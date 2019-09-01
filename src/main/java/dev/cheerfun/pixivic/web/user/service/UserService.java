package dev.cheerfun.pixivic.web.user.service;

import dev.cheerfun.pixivic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.verification.model.EmailBindingVerificationCode;
import dev.cheerfun.pixivic.web.user.exception.RegistrationException;
import dev.cheerfun.pixivic.web.user.exception.SignInException;
import dev.cheerfun.pixivic.web.user.exception.UserAuthException;
import dev.cheerfun.pixivic.web.user.mapper.UserMapper;
import dev.cheerfun.pixivic.web.user.model.User;
import dev.cheerfun.pixivic.web.user.util.EmailUtil;
import dev.cheerfun.pixivic.web.user.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 0:04
 * @description UserService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {
    private final UserMapper userMapper;
    private final JWTUtil jwtUtil;
    private final EmailUtil emailUtil;
    private final VerificationCodeService verificationCodeService;
    private final static String PIXIVIC = "Pixivic酱";
    private final static String CONTENT_1 = "点击以下按钮以验证邮箱";
    private final static String CONTENT_2 = "点击以下按钮以重置密码";

    public String signUp(User user) throws MessagingException {
        //检测用户名或邮箱是否重复
        if (userMapper.checkUserNameAndEmail(user.getUsername(), user.getEmail()) == 1) {
            throw new RegistrationException(HttpStatus.CONFLICT, "用户名或邮箱已存在");
        }
        user.init();
        userMapper.insertUser(user);
        //签发token
        Map<String, Object> claims = new HashMap<>();
        claims.put("permissionLevel", user.getPermissionLevel());
        claims.put("isBan", user.getIsBan());
        claims.put("refreshCount", 0);
        claims.put("userId", user.getUserId());
        //发送验证邮件
        EmailBindingVerificationCode emailVerificationCode = verificationCodeService.getEmailVerificationCode(user.getEmail());
        emailUtil.sendEmail(user.getEmail(), user.getUsername(), PIXIVIC, CONTENT_1, "https://pixivic.com/emailCheck?vid=" + emailVerificationCode.getVid() + "&value=" + emailVerificationCode.getValue());
        return jwtUtil.getToken(claims);
    }

    public User signIn(String username, String password) {
        User user = userMapper.getUser(username, PasswordUtil.generateSecurePassword(password));
        if (user == null) {
            throw new SignInException(HttpStatus.BAD_REQUEST, "用户名或密码不正确");
        }
        return user;
    }

    public boolean checkUsername(String username) {
        return userMapper.checkUserNameAndEmail(username, "") == 1;
    }

    public boolean checkEmail(String email) {
        return userMapper.checkUserNameAndEmail("", email) == 1;
    }

    public User signIn(String qqAccessToken) {
        return userMapper.getUserByQQAccessToken(qqAccessToken);
    }

    public int bindQQ(String qqAccessToken, String userId, String token) {
        checkUser(userId, token);
        return userMapper.setQqAccessToken(qqAccessToken, userId);
    }

    public int setAvatar(String avatar, String userId, String token) {
        checkUser(userId, token);
        return userMapper.setAvatar(avatar, userId);
    }

    private void checkUser(String userId, String token) {
        if (!userId.equals(jwtUtil.getAllClaimsFromToken(token).get("userId"))) {
            throw new UserAuthException(HttpStatus.UNAUTHORIZED, "token与操作对象不匹配");
        }
    }

    public int setEmail(String email, String userId) {
        return userMapper.setEmail(email, userId);
    }

    public int setPassword(String password, String userId,String email) {
        return userMapper.setPassword(password, userId,email);
    }

    public void getResetPasswordEmail(String email) throws MessagingException {
        if (checkEmail(email)) {
            EmailBindingVerificationCode emailVerificationCode = verificationCodeService.getEmailVerificationCode(email);
            emailUtil.sendEmail(email, "亲爱的用户", PIXIVIC, CONTENT_2, "https://pixivic.com/resetPassword?vid=" + emailVerificationCode.getVid() + "&value=" + emailVerificationCode.getValue());
        }
        throw new UserAuthException(HttpStatus.NOT_FOUND, "用户邮箱不存在");
    }
}
