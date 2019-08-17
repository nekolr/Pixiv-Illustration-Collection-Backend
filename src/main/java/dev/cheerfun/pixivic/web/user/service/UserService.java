package dev.cheerfun.pixivic.web.user.service;

import dev.cheerfun.pixivic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.web.user.exception.RegistrationException;
import dev.cheerfun.pixivic.web.user.exception.SignInException;
import dev.cheerfun.pixivic.web.user.exception.UserAuthException;
import dev.cheerfun.pixivic.web.user.mapper.UserMapper;
import dev.cheerfun.pixivic.web.user.model.User;
import dev.cheerfun.pixivic.web.user.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

    public String signUp(User user) {
        //检测用户名或邮箱是否重复
        if (userMapper.checkUserNameAndEmail(user.getUsername(), user.getEmail()) == 0) {
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
        return userMapper.getUser(qqAccessToken);
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

    public int setPassword(String password, String userId) {
        return userMapper.setPassword(password, userId);
    }
}
