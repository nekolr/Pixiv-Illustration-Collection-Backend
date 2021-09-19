package dev.cheerfun.pixivic.biz.web.user.service;

import com.google.common.base.Strings;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.auth.exception.AuthBanException;
import dev.cheerfun.pixivic.basic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.basic.event.constant.ActionType;
import dev.cheerfun.pixivic.basic.event.constant.ObjectType;
import dev.cheerfun.pixivic.basic.event.domain.Event;
import dev.cheerfun.pixivic.basic.sensitive.util.SensitiveFilter;
import dev.cheerfun.pixivic.basic.verification.domain.EmailBindingVerificationCode;
import dev.cheerfun.pixivic.biz.credit.customer.CreditEventCustomer;
import dev.cheerfun.pixivic.biz.sitemap.po.Url;
import dev.cheerfun.pixivic.biz.web.collection.service.CollectionService;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.common.exception.UserCommonException;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.illust.service.SearchService;
import dev.cheerfun.pixivic.biz.web.oauth2.service.OAuth2Service;
import dev.cheerfun.pixivic.biz.web.sentence.po.Sentence;
import dev.cheerfun.pixivic.biz.web.sentence.service.SentenceService;
import dev.cheerfun.pixivic.biz.web.user.dto.CheckInDTO;
import dev.cheerfun.pixivic.biz.web.user.dto.VerifiedDTO;
import dev.cheerfun.pixivic.biz.web.user.dto.VerifiedResponseResult;
import dev.cheerfun.pixivic.biz.web.user.mapper.CommonMapper;
import dev.cheerfun.pixivic.biz.web.user.util.PasswordUtil;
import dev.cheerfun.pixivic.biz.web.user.util.VerifiedUtil;
import dev.cheerfun.pixivic.biz.web.vip.constant.ExchangeCodeBizType;
import dev.cheerfun.pixivic.biz.web.vip.service.ExchangeCodeService;
import dev.cheerfun.pixivic.biz.web.vip.service.VIPUserService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Picture;
import dev.cheerfun.pixivic.common.util.email.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 0:04
 * @description UserService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonService {
    private final static String AVATAR_PRE = "https://static.sharemoe.net/avatar/299x299/";
    private final static String AVATAR_POS = ".jpg";
    private final static String PIXIVIC = "虾萌酱";
    private final static String CONTENT_1 = "点击以下按钮以验证邮箱";
    private final static String CONTENT_2 = "点击以下按钮以重置密码";
    private final static String QQ_BIND_URL_PRE = "https://graph.qq.com/oauth2.0/me?access_token=";
    private final CommonMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final HttpClient httpClient;
    private final PasswordUtil passwordUtil;
    private final EmailUtil emailUtil;
    private final VerificationCodeService verificationCodeService;
    private final CreditEventCustomer creditEventCustomer;
    private final SearchService searchService;
    private final SentenceService sentenceService;
    private final SensitiveFilter sensitiveFilter;
    private final CollectionService collectionService;
    private ExchangeCodeService exchangeCodeService;
    private OAuth2Service oAuth2Service;

    @Autowired
    public void setoAuth2Service(OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
    }

    @Autowired
    public void setExchangeCodeService(ExchangeCodeService exchangeCodeService) {
        this.exchangeCodeService = exchangeCodeService;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "checkUsername", key = "#user.username"),
            @CacheEvict(value = "checkEmail", key = "#user.email"),
            @CacheEvict(value = "checkPhone", key = "#user.phone")
    })
    public User signUp(User user, String exchangeCode) {
        //检测用户名或邮箱是否重复
        if (userMapper.checkUserName(user.getUsername()) == 1 || userMapper.checkUserEmail(user.getEmail()) == 1 || userMapper.checkUserPhone(user.getPhone()) == 1) {
            throw new UserCommonException(HttpStatus.CONFLICT, "用户、邮箱或手机号已存在");
        }
        user.setPassword(passwordUtil.encrypt(user.getPassword()));
        user.init();
        userMapper.insertUser(user);
        exchangeCodeService.exchangeCode(user.getId(), exchangeCode, ExchangeCodeBizType.INVITE);
        //签发token
        //发送验证邮件
        EmailBindingVerificationCode emailVerificationCode = verificationCodeService.getEmailVerificationCode(user.getEmail());
        emailUtil.sendEmail(user.getEmail(), user.getUsername(), PIXIVIC, CONTENT_1, "https://pixivic.com/emailCheck?vid=" + emailVerificationCode.getVid() + "&value=" + emailVerificationCode.getValue() + "&userId=" + user.getId() + "&email=" + user.getEmail());
        user = queryUser(userMapper.queryUserByusernameAndPassword(user.getUsername(), user.getPassword()));
        userMapper.setAvatar(AVATAR_PRE + user.getId() + AVATAR_POS, user.getId());
        //初始化汇总表
        userMapper.initSummary(user.getId());
        user.setAvatar(AVATAR_PRE + user.getId() + AVATAR_POS);
        return user;
    }

    public User signIn(String username, String password) {
        Integer uid = userMapper.queryUserByusernameAndPassword(username, passwordUtil.encrypt(password));
        if (uid == null) {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "用户名或密码不正确");
        }
        if (stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ACCOUNT_BAN_SET, String.valueOf(uid))) {
            throw new AuthBanException(HttpStatus.FORBIDDEN, "账户异常");
        }
        return queryUser(uid);
    }

    @Cacheable("checkUsername")
    public boolean checkUsername(String username) {
        return userMapper.checkUserName(username) == 1;
    }

    @Cacheable("checkEmail")
    public boolean checkEmail(String email) {
        return userMapper.checkUserEmail(email) == 1;
    }

    @Cacheable("checkPhone")
    public boolean checkPhone(String phone) {
        return userMapper.checkUserPhone(phone) == 1;
    }

    public User signIn(String qqAccessToken) throws IOException, InterruptedException {
        String qqOpenId = getQQOpenId(qqAccessToken);
        Integer uid = userMapper.getUserByQQOpenId(qqOpenId);
        if (stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ACCOUNT_BAN_SET, String.valueOf(uid))) {
            throw new AuthBanException(HttpStatus.FORBIDDEN, "账户异常");
        }
        if (uid == null) {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "不存在此QQ绑定的帐号");
        }
        return queryUser(uid);
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

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#userId"),
            @CacheEvict(value = "checkEmail", key = "#email")
    })
    public void setEmail(String email, int userId) {
        userMapper.setEmail(email, userId);
    }

    @CacheEvict(value = "users", key = "#userId")
    public void checkEmail(String email, int userId) {
        userMapper.checkEmail(email, userId);
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
        User user = queryUser(userId);
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
        User user = queryUser(userId);
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
        Integer uid = userMapper.queryUserByEmail(emailAddr);
        if (uid == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        return queryUser(uid);
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
        if (user.getPermissionLevel() >= PermissionLevel.VIP && user.getPermissionLevelExpireDate() != null && user.getPermissionLevelExpireDate().isAfter(LocalDateTime.now())) {
            //如果是则叠加
            userMapper.extendPermissionLevelExpirationTime(userId, type);
            return true;
        } else {
            //如果不是则过期时间为当前时间加上type
            userMapper.updatePermissionLevelExpirationTime(userId, user.getPermissionLevel() > PermissionLevel.VIP ? user.getPermissionLevel() : PermissionLevel.VIP, LocalDateTime.now().plusHours(type * 24));
            return true;
        }
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
        List<Illustration> illustrationList = searchService.searchByKeyword(sentence.getOriginateFromJP() != null ? sentence.getOriginateFromJP() : sentence.getOriginateFrom(), 10, 1, "original", null, null, null, null, null, 0, null, null, null, 5, null).get();
        if (illustrationList != null && illustrationList.size() > 0) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            illustration = illustrationList.get(random.nextInt(illustrationList.size()));
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

    public String checkUsernameSensitive(String username) {
        return sensitiveFilter.filter(username);
    }

    @Transactional
    public User updateUsername(Integer userId, String username) {
        //校验敏感词
        username = sensitiveFilter.filter(username);
        if (username.contains("*")) {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "用户名中包含非法关键词或者*");
        }
        //校验改名记录表 每半年只能改一次
        if (checkUserUpdateUsernameLog(userId)) {
            updateUsernameToDb(userId, username);
            //增加改名记录
            insertUserUpdateUsernameLog(userId);
            //清理缓存（主要是用户发布相关的，例如画集、讨论需要即时清理）
            collectionService.evictCacheByUser(userId);
            return queryUser(userId);
        } else {
            throw new UserCommonException(HttpStatus.BAD_REQUEST, "用户名每半年只能修改一次");
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "checkUserUpdateUsernameLog", key = "#userId")
    })
    @Transactional
    public void insertUserUpdateUsernameLog(Integer userId) {
        userMapper.insertUserUpdateUsernameLog(userId);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#userId"),
            @CacheEvict(value = "checkUsername", key = "#username")
    })
    @Transactional
    public void updateUsernameToDb(Integer userId, String username) {
        userMapper.updateUsername(userId, username);
        userMapper.updateUsernameInCollections(userId, username);
        userMapper.updateFromUsernameInComments(userId, username);
        userMapper.updateToUsernameInComments(userId, username);
        userMapper.updateUsernameInDiscussions(userId, username);
        userMapper.updateUsernameInRewards(userId, username);
        userMapper.updateUsernameInUserArtistFollowed(userId, username);
        userMapper.updateUsernameInUserCollectionBookmarked(userId, username);
        userMapper.updateUsernameInUserIllustBookmarked(userId, username);
        userMapper.updateUsernameInUserUserFollowed(userId, username);
    }

    @Cacheable("checkUserUpdateUsernameLog")
    public boolean checkUserUpdateUsernameLog(Integer userId) {
        LocalDateTime localDateTime = userMapper.queryUserUpdateUsernameLog(userId);
        //没修改过,或者在三个月之前
        return localDateTime == null || localDateTime.toLocalDate().isBefore(LocalDate.now().plusMonths(-6));
    }

    @CacheEvict(value = "users", key = "#userId")
    public void setPhone(String phone, int userId) {
        userMapper.setPhone(phone, userId);
    }

    public User signInWithPhone(String phone) {
        return queryUser(userMapper.queryUserByPhone(phone));
    }

    public boolean validatePassword(int userId, String oldPassword) {
        User user = queryUser(userId);
        return passwordUtil.encrypt(oldPassword).equals(user.getPassword());
    }

    public String generateDiscussToken(int userId) throws IOException, InterruptedException {
        HttpRequest build = HttpRequest.newBuilder()
                .uri(URI.create("https://discuss.sharemoe.net/auth/passport")).GET()
                .build();
        HttpResponse<String> response = httpClient.send(build, HttpResponse.BodyHandlers.ofString());
        String locationUrl = response.headers().firstValue("location").get();
        String cookie = response.headers().firstValue("set-cookie").get();
        String flarumSession = cookie.split(";")[0];
        Map<String, List<String>> param = splitQuery(new URL(locationUrl));
        String authorizeUrl = oAuth2Service.authorize(Integer.valueOf(param.get("client_id").get(0)), param.get("state").get(0), param.get("redirect_uri").get(0));
        HttpRequest queryDiscussToken = HttpRequest.newBuilder()
                .uri(URI.create(authorizeUrl)).GET()
                .header("Cookie", flarumSession)
                .build();
        HttpResponse<String> queryDiscussTokenResponse = httpClient.send(queryDiscussToken, HttpResponse.BodyHandlers.ofString());
        String discussCookie = queryDiscussTokenResponse.headers().firstValue("set-cookie").get();
        if (!discussCookie.contains("flarum_remember")) {
            log.info("用户未注册论坛，将进行自动注册");
            final String body = queryDiscussTokenResponse.body();
            String token = body.substring(body.indexOf("\"token\":\"") + 9, body.indexOf("\",\"provided\""));
            //自动注册
            User user = queryUser(userId);
            HttpRequest register = HttpRequest.newBuilder()
                    .uri(URI.create("https://discuss.sharemoe.net/register")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"" + user.getUsername() + "\",\"email\":\"" + user.getEmail() + "\",\"token\":\"" + token + "\"}"))
                    .headers("Cookie", flarumSession)
                    .build();
            HttpResponse<String> registerResponse = httpClient.send(register, HttpResponse.BodyHandlers.ofString());
            if (registerResponse.statusCode() == 422) {
                log.info("{\"username\":\"" + user.getUsername() + "\",\"email\":\"" + user.getEmail() + "\",\"token\":\"" + token + "\"}");
                log.info(registerResponse.body());
                log.info("用户论坛用户名重复，进行重新注册");
                register = HttpRequest.newBuilder()
                        .uri(URI.create("https://discuss.sharemoe.net/register")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"" + "uid_" + userId + "\",\"email\":\"" + user.getEmail() + "\",\"token\":\"" + token + "\"}"))
                        .headers("Cookie", flarumSession)
                        .build();
                registerResponse = httpClient.send(register, HttpResponse.BodyHandlers.ofString());
                if (registerResponse.statusCode() != 200) {
                    log.info(registerResponse.body());
                }
            }
            discussCookie = registerResponse.headers().firstValue("set-cookie").get();
        }
        return discussCookie.split(";")[0].replace("flarum_remember=", "");
    }

    public Map<String, List<String>> splitQuery(URL url) {
        if (Strings.isNullOrEmpty(url.getQuery())) {
            return Collections.emptyMap();
        }
        return Arrays.stream(url.getQuery().split("&"))
                .map(e -> {
                    try {
                        return splitQueryParameter(e);
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    public AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) throws UnsupportedEncodingException {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        return new AbstractMap.SimpleImmutableEntry<>(
                URLDecoder.decode(key, "UTF-8"),
                URLDecoder.decode(value, "UTF-8")
        );
    }
}
