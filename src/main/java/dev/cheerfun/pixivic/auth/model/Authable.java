package dev.cheerfun.pixivic.auth.model;

import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/08 22:24
 * @description 权限约定
 */
public interface Authable {

    String getIssuer();

    Map<String, Object> getClaims();

    boolean isEnabled();

}
