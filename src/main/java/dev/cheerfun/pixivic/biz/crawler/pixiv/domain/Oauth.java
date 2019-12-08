package dev.cheerfun.pixivic.biz.crawler.pixiv.domain;

import lombok.Data;

@Data
public class Oauth {
    private volatile String access_token;
    private String username;
    private String password;
    private volatile Boolean isBan=false;
    private String param;
    private long banAt;

}
