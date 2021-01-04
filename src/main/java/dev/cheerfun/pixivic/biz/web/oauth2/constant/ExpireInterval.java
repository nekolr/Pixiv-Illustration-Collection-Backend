package dev.cheerfun.pixivic.biz.web.oauth2.constant;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/4 11:38 AM
 * @description ExpireTime
 */
public class ExpireInterval {
    public static final long CODE_EXPIRE_INTERVAL = 1000 * 60 * 60;
    public static final long ACCESS_TOKEN_EXPIRE_INTERVAL = 60 * 60L;
    public static final long ACCESS_TOKEN_REFRESH_EXPIRE_INTERVAL = 60 * 60 * 12L;
    public static final long REFRESH_TOKEN_EXPIRE_INTERVAL = 60 * 60 * 24 * 30;

}
