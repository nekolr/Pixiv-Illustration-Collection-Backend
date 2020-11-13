package dev.cheerfun.pixivic.biz.web.user.service;

import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.basic.event.constant.ActionType;
import dev.cheerfun.pixivic.basic.event.constant.ObjectType;
import dev.cheerfun.pixivic.basic.event.domain.Event;
import dev.cheerfun.pixivic.basic.verification.domain.EmailBindingVerificationCode;
import dev.cheerfun.pixivic.biz.credit.customer.CreditEventCustomer;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.common.exception.UserCommonException;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.biz.web.illust.service.SearchService;
import dev.cheerfun.pixivic.biz.web.sentence.po.Sentence;
import dev.cheerfun.pixivic.biz.web.sentence.service.SentenceService;
import dev.cheerfun.pixivic.biz.web.user.dto.CheckInDTO;
import dev.cheerfun.pixivic.biz.web.user.mapper.CommonMapper;
import dev.cheerfun.pixivic.biz.web.user.util.PasswordUtil;
import dev.cheerfun.pixivic.biz.web.user.util.RecaptchaUtil;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Picture;
import dev.cheerfun.pixivic.common.util.email.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 0:04
 * @description UserService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonService {
    private final static String AVATAR_PRE = "https://static.pixivic.net/avatar/299x299/";
    private final static String AVATAR_POS = ".jpg";
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
    private final CreditEventCustomer creditEventCustomer;
    private final SearchService searchService;
    private final SentenceService sentenceService;
    private final IllustrationBizService illustrationBizService;
    private final Random random = new Random(21);
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

    @CacheEvict(value = "users", key = "#userId")
    public User bindQQ(String qqAccessToken, int userId) throws IOException, InterruptedException {
        String qqOpenId = getQQOpenId(qqAccessToken);
        userMapper.setQQOpenId(qqOpenId, userId);
        return userMapper.queryUserByUserId(userId);
    }

    public int setAvatar(String avatar, int userId) {
        return userMapper.setAvatar(avatar, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "users", key = "#userId")
    public User setEmail(String email, int userId) {
        userMapper.setEmail(email, userId);
        return userMapper.queryUserByUserId(userId);
    }

    public int setPasswordByEmail(String password, String email) {
        User user = queryUserByEmail(email);
        if (user != null) {
            return setPasswordById(passwordUtil.encrypt(password), user.getId());
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "邮箱不存在");
    }

    public void getResetPasswordEmail(String email) {
        //if (checkEmail(email)) {
        EmailBindingVerificationCode emailVerificationCode = verificationCodeService.getEmailVerificationCode(email);
        emailUtil.sendEmail(email, "亲爱的用户", PIXIVIC, CONTENT_2, "https://pixivic.com/resetPassword?vid=" + emailVerificationCode.getVid() + "&value=" + emailVerificationCode.getValue());
       /* } else {
            throw new UserCommonException(HttpStatus.NOT_FOUND, "用户邮箱不存在");
        }*/
    }

    public void getCheckEmail(String email, int userId) {
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

    @CacheEvict(value = "users", key = "#userId")
    public int setPasswordById(String password, Integer userId) {
        return userMapper.setPasswordById(password, userId);
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
        User user = queryUser(usrId);
        if (user != null) {
            return user.getIsCheckEmail();
        }
        throw new UserCommonException(HttpStatus.BAD_REQUEST, "用户不存在");
    }

    public Boolean queryIsBindQQ(int userId) {
        User user = userMapper.queryUserByUserId(userId);
        return user.getIsBindQQ();
    }

    @Cacheable(value = "users", key = "#userId")
    public User queryUser(Integer userId) {
        User user = userMapper.queryUserByUserId(userId);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        return
                user;
    }

    public User queryUserByEmail(String emailAddr) {
        User user = userMapper.queryUserByEmail(emailAddr);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        return
                user;
    }

    @CacheEvict(value = "users", key = "#userId")
    public void updateUserInfo(int userId, User user) {
        userMapper.updateUserInfo(userId, user.getGender(), user.getSignature(), user.getLocation());
    }

    @CacheEvict(value = "users", key = "#userId")
    public Boolean unbindQQ(int userId) {
        userMapper.unbindQQ(userId);
        return true;
    }

    public Boolean uploadModuleImageLog(Picture picture) {
        return userMapper.uploadModuleImageLog(picture.getUploadFrom(), picture.getUuid(), picture.getModuleName()) == 1;
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public Boolean updateUserPermissionLevel(Integer userId, byte type) {
        User user = queryUser(userId);
        //首先查询用户是否会员且未过期
        if (user.getPermissionLevel() == PermissionLevel.VIP && user.getPermissionLevelExpireDate() != null && user.getPermissionLevelExpireDate().isAfter(LocalDateTime.now())) {
            //如果是则叠加
            userMapper.extendPermissionLevelExpirationTime(userId, type);
            return true;
        } else {
            //如果不是则过期时间为当前时间加上type
            userMapper.updatePermissionLevelExpirationTime(userId, PermissionLevel.VIP, LocalDateTime.now().plusHours(type * 24));
            return true;
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void refreshUserPermissionLevel() {
        userMapper.refreshUserPermissionLevel();
    }

    @CacheEvict(value = "users", key = "#userId")
    public Boolean modifyUserPoint(Integer userId, int star) {
        return userMapper.modifyUserPoint(userId, star) == 1;
    }

    @Transactional
    public CheckInDTO dailyCheckIn(Integer userId) throws ExecutionException, InterruptedException {
        //检查当天是否校验过
        if (queryCheckInStatus(userId)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "请勿重复签到");
        }
        //签到
        checkIn(userId);
        //加分
        LocalDate now = LocalDate.now();
        Integer score = creditEventCustomer.consumeSync(new Event(userId, ActionType.CHECK_IN, ObjectType.ATTENDANCES, Integer.valueOf(now.toString().replace("-", "")), LocalDateTime.now()));
        //随机获取动漫台词
        Sentence sentence = sentenceService.queryRandomSentence();
        //根据台词来源作品获取画作
        Illustration illustration = null;
        List<Integer> illustrationList = searchService.searchByKeyword(sentence.getOriginateFromJP() != null ? sentence.getOriginateFromJP() : sentence.getOriginateFrom(), 10, 1, "original", null, null, null, null, null, 0, null, null, null, 5, null).get();
        if (illustrationList != null && illustrationList.size() > 0) {
            illustration = illustrationBizService.queryIllustrationByIdFromDb(illustrationList.get(random.nextInt(illustrationList.size())));
        }
        return new CheckInDTO(score, sentence, illustration);
    }

    public Boolean queryCheckInStatus(Integer uerId) {
        return LocalDate.now().toString().equals(queryRecentCheckDate(uerId));
    }

    @Cacheable(value = "userRecentCheckDate", key = "#userId")
    public String queryRecentCheckDate(Integer userId) {
        return userMapper.queryRecentCheckDate(userId);
    }

    @CacheEvict(value = "userRecentCheckDate", key = "#userId")
    public void checkIn(Integer userId) {
        userMapper.checkIn(userId, LocalDate.now().toString());
    }

}
