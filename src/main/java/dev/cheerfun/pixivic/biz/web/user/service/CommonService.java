package dev.cheerfun.pixivic.biz.web.user.service;

import dev.cheerfun.pixivic.basic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.basic.verification.domain.EmailBindingVerificationCode;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.common.exception.UserCommonException;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.user.mapper.CommonMapper;
import dev.cheerfun.pixivic.biz.web.user.util.PasswordUtil;
import dev.cheerfun.pixivic.common.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 0:04
 * @description UserService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonService {
    private final CommonMapper userMapper;
    private final HttpClient httpClient;
    private final JWTUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final EmailUtil emailUtil;
    private final VerificationCodeService verificationCodeService;
    private final static String AVATAR_PRE = "https://pic.pixivic.com/";
    private final static String AVATAR_POS = ".png";
    private final static String PIXIVIC = "Pixivic酱";
    private final static String CONTENT_1 = "点击以下按钮以验证邮箱";
    private final static String CONTENT_2 = "点击以下按钮以重置密码";
    private final static String QQ_BIND_URL_PRE = "https://graph.qq.com/oauth2.0/me?access_token=";

    public User signUp(User user) throws MessagingException {
        //检测用户名或邮箱是否重复
        if (userMapper.checkUserNameAndEmail(user.getUsername(), user.getEmail()) == 1) {
            throw new UserCommonException(HttpStatus.CONFLICT, "用户名或邮箱已存在");
        }
        user.setPassword(passwordUtil.encrypt(user.getPassword()));
        user.init();
        userMapper.insertUser(user);
        //签发token
        //发送验证邮件
        EmailBindingVerificationCode emailVerificationCode = verificationCodeService.getEmailVerificationCode(user.getEmail());
        emailUtil.sendEmail(user.getEmail(), user.getUsername(), PIXIVIC, CONTENT_1, "https://pixivic.com/emailCheck?vid=" + emailVerificationCode.getVid() + "&value=" + emailVerificationCode.getValue() + "&userId=" + user.getId() + "&email=" + user.getEmail());
        user = userMapper.queryUserByusernameAndPassword(user.getUsername(), user.getPassword());
        userMapper.setAvatar(AVATAR_PRE + user.getId() + AVATAR_POS, user.getId());
        user.setAvatar(AVATAR_PRE + user.getId() + AVATAR_POS);
        return user;
    }

    public User signIn(String username, String password) {
        User user = userMapper.queryUserByusernameAndPassword(username, passwordUtil.encrypt(password));
        if (user == null) {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "用户名或密码不正确");
        }
        return user;
    }

    public boolean checkUsername(String username) {
        return userMapper.checkUserNameAndEmail(username, "") == 1;
    }

    public boolean checkEmail(String email) {
        return userMapper.checkUserNameAndEmail("", email) == 1;
    }

    public User signIn(String qqAccessToken) throws IOException, InterruptedException {
        return userMapper.getUserByQQOpenId(getQQOpenId(qqAccessToken));
    }

    public int bindQQ(String qqAccessToken, int userId) throws IOException, InterruptedException {
        String qqOpenId = getQQOpenId(qqAccessToken);
        return userMapper.setQQOpenId(qqOpenId, userId);
    }

    public int setAvatar(String avatar, int userId) {
        return userMapper.setAvatar(avatar, userId);
    }

    @Transactional
    public User setEmail(String email, int userId) {
        userMapper.setEmail(email, userId);
        return userMapper.queryUserByUserId(userId);
    }

    public int setPasswordByEmail(String password, String email) {
        return userMapper.setPasswordByEmail(passwordUtil.encrypt(password), email);
    }

    public void getResetPasswordEmail(String email) throws MessagingException {
        if (checkEmail(email)) {
            EmailBindingVerificationCode emailVerificationCode = verificationCodeService.getEmailVerificationCode(email);
            emailUtil.sendEmail(email, "亲爱的用户", PIXIVIC, CONTENT_2, "https://pixivic.com/resetPassword?vid=" + emailVerificationCode.getVid() + "&value=" + emailVerificationCode.getValue());
        } else {
            throw new UserCommonException(HttpStatus.NOT_FOUND, "用户邮箱不存在");
        }
    }

    public void getCheckEmail(String email, int userId) throws MessagingException {
        User user = userMapper.queryUserByUserId(userId);
        EmailBindingVerificationCode emailVerificationCode = verificationCodeService.getEmailVerificationCode(email);
        emailUtil.sendEmail(email, user.getUsername(), PIXIVIC, CONTENT_1, "https://pixivic.com/emailCheck?vid=" + emailVerificationCode.getVid() + "&value=" + emailVerificationCode.getValue() + "&userId=" + userId + "&email=" + email);
    }

    public String getEmail(HttpServletRequest request) {
        final String path =
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern =
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        String arguments = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path);
        String moduleName;
        if (!arguments.isEmpty() && arguments.contains("/")) {
            moduleName = arguments.substring(0, arguments.lastIndexOf("/"));
        } else {
            moduleName = "";
        }
        return moduleName;

    }

    public int setPasswordById(String password, int userId) {
        return userMapper.setPasswordById(passwordUtil.encrypt(password), userId);
    }

    public String getQQOpenId(String qqAccessToken) throws IOException, InterruptedException {
        HttpRequest build = HttpRequest.newBuilder()
                .uri(URI.create(QQ_BIND_URL_PRE + qqAccessToken)).GET()
                .build();
        String body = httpClient.send(build, HttpResponse.BodyHandlers.ofString()).body();
        if (body != null && body.contains("openid")) {
            int i = body.indexOf("\"openid\":\"");
            return body.substring(i + 10, i + 42);
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "access_token不正确");
    }

    public Boolean queryEmailIsCheck(int usrId) {
        User user = userMapper.queryUserByUserId(usrId);
        if (user != null) {
            return user.getPermissionLevel() > 1;
        }
        throw new UserCommonException(HttpStatus.BAD_REQUEST, "用户不存在");
    }

    public Boolean queryIsBindQQ(int userId) {
        User user = userMapper.queryUserByUserId(userId);
        return user.getIsBindQQ();
    }
}
