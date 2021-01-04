package dev.cheerfun.pixivic.biz.web.oauth2.service;

import dev.cheerfun.pixivic.basic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.oauth2.config.OauthAuthorizationServer;
import dev.cheerfun.pixivic.biz.web.oauth2.constant.ExpireInterval;
import dev.cheerfun.pixivic.biz.web.oauth2.constant.GrantType;
import dev.cheerfun.pixivic.biz.web.oauth2.constant.Scope;
import dev.cheerfun.pixivic.biz.web.oauth2.constant.TokenType;
import dev.cheerfun.pixivic.biz.web.oauth2.domain.OAuth2TokenResponse;
import dev.cheerfun.pixivic.biz.web.oauth2.mapper.OAuth2Mapper;
import dev.cheerfun.pixivic.biz.web.oauth2.po.OAuth2Client;
import dev.cheerfun.pixivic.biz.web.user.service.CommonService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/4 11:08 AM
 * @description OAuth2Service
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OAuth2Service {
    private final CommonService userService;
    private final JWTUtil jwtUtil;
    private final OauthAuthorizationServer oauthAuthorizationServer;
    private final OAuth2Mapper oAuth2Mapper;
    private Map<String, Long> responseCodeMap;
    private Map<Integer, OAuth2Client> oAuth2ClientMap;

    @PostConstruct
    public void init() {
        responseCodeMap = new ConcurrentHashMap<>();
        oAuth2ClientMap = oAuth2Mapper.queryOAuth2ClientList().stream().collect(Collectors.toMap(OAuth2Client::getClientId, e -> e));
    }

    public OauthAuthorizationServer queryServerInfo() {
        return oauthAuthorizationServer;
    }

    public OAuth2Client queryOAuth2Client(Integer clientId) {
        return oAuth2ClientMap.get(clientId);
    }

    public String authorize(Integer clientId, String state, String redirectUri) {
        OAuth2Client oAuth2Client = queryOAuth2Client(clientId);
        if (oAuth2Client == null || !oAuth2Client.getRedirectUri().equals(redirectUri)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "无效客户端");
        }
        String code = UUID.randomUUID().toString() + "-" + AppContext.get().get(AuthConstant.USER_ID);
        responseCodeMap.put(code, System.currentTimeMillis());
        return redirectUri + "?code=" + code + "&state=" + state;
    }

    public OAuth2TokenResponse token(Integer clientId, String clientSecret, String grantType, String refreshToken, String code, String redirectUri) {
        OAuth2Client oAuth2Client = queryOAuth2Client(clientId);
        if (oAuth2Client == null || !oAuth2Client.getRedirectUri().equals(redirectUri) || !oAuth2Client.getClientSecret().equals(clientSecret)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "无效客户端");
        }
        switch (grantType) {
            case GrantType.AUTHORIZATION_CODE: {
                Long createTime = responseCodeMap.get(code);
                if (createTime != null && System.currentTimeMillis() - createTime < ExpireInterval.CODE_EXPIRE_INTERVAL) {
                    //取出userId
                    Integer userId = Integer.parseInt(code.substring(code.lastIndexOf("-") + 1));
                    String accessToken = jwtUtil.getToken(userService.queryUser(userId), ExpireInterval.ACCESS_TOKEN_EXPIRE_INTERVAL);
                    refreshToken = jwtUtil.getToken(userService.queryUser(userId), ExpireInterval.REFRESH_TOKEN_EXPIRE_INTERVAL);
                    responseCodeMap.remove(code);
                    return new OAuth2TokenResponse(accessToken, TokenType.BEARER, ExpireInterval.ACCESS_TOKEN_EXPIRE_INTERVAL, refreshToken, Scope.USER_INFO, null);
                } else {
                    responseCodeMap.remove(code);
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "无效授权码");
                }
            }
            case GrantType.REFRESH_TOKEN: {
                //校验refresh
                Map<String, Object> claim = jwtUtil.validateToken(refreshToken);
                //颁发新的
                return new OAuth2TokenResponse(jwtUtil.refreshToken((Claims) claim), TokenType.BEARER, ExpireInterval.ACCESS_TOKEN_REFRESH_EXPIRE_INTERVAL, refreshToken, Scope.USER_INFO, null);
            }
            default:
                throw new BusinessException(HttpStatus.BAD_REQUEST, "无效授方式");
        }
    }

    public User userinfo(String accessToken) {
        Map<String, Object> claim = jwtUtil.validateToken(accessToken.replace("Bearer ", ""));
        return userService.queryUser((int) claim.get(AuthConstant.USER_ID));
    }
}
