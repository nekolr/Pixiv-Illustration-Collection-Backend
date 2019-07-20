package dev.cheerfun.pixivic.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/20 11:06
 * @description QQOAuth2
 */
@Data
public class QQOAuth2 {
    private String uid;
    @JsonProperty("access_token")
    private String accessToken;
    private String openid;
}
