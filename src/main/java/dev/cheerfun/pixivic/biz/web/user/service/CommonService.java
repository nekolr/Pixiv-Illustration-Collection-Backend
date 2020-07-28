package dev.cheerfun.pixivic.biz.web.user.service;

import dev.cheerfun.pixivic.basic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.basic.verification.domain.EmailBindingVerificationCode;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.common.exception.UserCommonException;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.user.mapper.CommonMapper;
import dev.cheerfun.pixivic.biz.web.user.util.PasswordUtil;
import dev.cheerfun.pixivic.common.po.Picture;
import dev.cheerfun.pixivic.common.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.gm4java.engine.GMException;
import org.gm4java.engine.GMServiceException;
import org.gm4java.engine.support.PooledGMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 0:04
 * @description UserService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonService {
    private final static String AVATAR_PRE = "https://pic.pixivic.com/";
    private final static String AVATAR_POS = ".png";
    private final static String PIXIVIC = "Pixivic酱";
    private final static String CONTENT_1 = "点击以下按钮以验证邮箱";
    private final static String CONTENT_2 = "点击以下按钮以重置密码";
    private final static String QQ_BIND_URL_PRE = "https://graph.qq.com/oauth2.0/me?access_token=";
    private final CommonMapper userMapper;
    private final HttpClient httpClient;
    private final JWTUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final EmailUtil emailUtil;
    private final VerificationCodeService verificationCodeService;
    //private final PooledGMService pooledGMService;

    public User signUp(User user) {
        //检测用户名或邮箱是否重复
        if (userMapper.checkUserName(user.getUsername()) == 1 || userMapper.checkUserEmail(user.getEmail()) == 1) {
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
        //初始化汇总表
        userMapper.initSummary(user.getId());
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
        return userMapper.checkUserName(username) == 1;
    }

    public boolean checkEmail(String email) {
        return userMapper.checkUserEmail(email) == 1;
    }

    public User signIn(String qqAccessToken) throws IOException, InterruptedException {
        String qqOpenId = getQQOpenId(qqAccessToken);
        User user = userMapper.getUserByQQOpenId(qqOpenId);
        if (user == null) {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "不存在此QQ绑定的帐号");
        }
        return user;
    }

    public User bindQQ(String qqAccessToken, int userId) throws IOException, InterruptedException {
        String qqOpenId = getQQOpenId(qqAccessToken);
        userMapper.setQQOpenId(qqOpenId, userId);
        return userMapper.queryUserByUserId(userId);
    }

    public int setAvatar(String avatar, int userId) {
        return userMapper.setAvatar(avatar, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public User setEmail(String email, int userId) {
        userMapper.setEmail(email, userId);
        return userMapper.queryUserByUserId(userId);
    }

    public int setPasswordByEmail(String password, String email) {
        return userMapper.setPasswordByEmail(passwordUtil.encrypt(password), email);
    }

    public void getResetPasswordEmail(String email) throws MessagingException {
        //if (checkEmail(email)) {
        EmailBindingVerificationCode emailVerificationCode = verificationCodeService.getEmailVerificationCode(email);
        emailUtil.sendEmail(email, "亲爱的用户", PIXIVIC, CONTENT_2, "https://pixivic.com/resetPassword?vid=" + emailVerificationCode.getVid() + "&value=" + emailVerificationCode.getValue());
       /* } else {
            throw new UserCommonException(HttpStatus.NOT_FOUND, "用户邮箱不存在");
        }*/
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
            return user.getIsCheckEmail();
        }
        throw new UserCommonException(HttpStatus.BAD_REQUEST, "用户不存在");
    }

    public Boolean queryIsBindQQ(int userId) {
        User user = userMapper.queryUserByUserId(userId);
        return user.getIsBindQQ();
    }

    public User queryUser(Integer userId) {
        User user = userMapper.queryUserByUserId(userId);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        return
                user;
    }

    public void updateUserInfo(int userId, User user) {
        userMapper.updateUserInfo(userId, user.getGender(), user.getSignature(), user.getLocation());
    }

    public Boolean unbindQQ(int userId) {
        userMapper.unbindQQ(userId);
        return true;
    }

    public Boolean uploadModuleImageLog(Picture picture) {
        return userMapper.uploadModuleImageLog(picture.getUploadFrom(), picture.getUuid(), picture.getModuleName()) == 1;
    }

   /* public Picture uploadModuleImage(String moduleName, MultipartFile file, int userId) {
        if (file == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "文件为空");
        }
        String webDir = "/Users/oysterqaq/Desktop";
        String imageUUID = UUID.randomUUID().toString();
        String originalFileName = Paths.get(webDir, moduleName, imageUUID + ".jpg").toString();
        String targetFileName = Paths.get(webDir, moduleName, imageUUID).toString();
        try {
            byte[] bytes = file.getBytes();
            Files.write(Paths.get(originalFileName), bytes, StandardOpenOption.CREATE);
            //gm处理
            //900一个档次
            pooledGMService.execute("convert " + originalFileName + " -thumbnail \"1200x1200>\" " + targetFileName + "_1200.jpg");
            //500一个档次
            pooledGMService.execute("convert " + originalFileName + " -thumbnail \"600x600>\" " + targetFileName + "_600.jpg");
            //500方图
            pooledGMService.execute("convert -size 200x200 " + originalFileName + "  -thumbnail 500x500^ -gravity center -extent 500x500 +profile \"*\" " + targetFileName + "_500_s.jpg");
            //http调用记录用户上传记录
            return new Picture(imageUUID, userId);
        } catch (IOException | GMException | GMServiceException e) {
            e.printStackTrace();
//            try {
//                Files.delete(Paths.get(originalFileName));
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
            throw new BusinessException(HttpStatus.BAD_REQUEST, "文件上传失败");
        }
    }*/
}
