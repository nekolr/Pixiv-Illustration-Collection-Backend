package dev.cheerfun.pixivic.biz.web.oauth2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/4 1:26 PM
 * @description OAuth2TokenResponse
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private long expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String scope;
    private String state;
}
