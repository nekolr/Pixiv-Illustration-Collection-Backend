package dev.cheerfun.pixivic.auth.model;

import io.jsonwebtoken.Claims;

import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/08 22:24
 * @description aop权限校验处理
 */
public interface Authable {
    String getIssuer();

    Map<String, Object> getClaims();

    boolean isEnabled();

    Authable castToAuthable(Claims claims);
}
