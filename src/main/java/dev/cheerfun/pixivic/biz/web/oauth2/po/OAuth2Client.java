package dev.cheerfun.pixivic.biz.web.oauth2.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/4 11:25 AM
 * @description OAuth2Client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2Client {
    private Integer clientId;
    private String clientSecret;
    private String redirectUri;
}
