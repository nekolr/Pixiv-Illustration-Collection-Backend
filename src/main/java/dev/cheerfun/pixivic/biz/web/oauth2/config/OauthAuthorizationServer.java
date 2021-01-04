package dev.cheerfun.pixivic.biz.web.oauth2.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/4 11:14 AM
 * @description OauthAuthorizationServer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "oauth2")
public class OauthAuthorizationServer {
    private String issuer;
    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;
    @JsonProperty("token_endpoint")
    private String tokenEndpoint;
    @JsonProperty("check_token")
    private String checkToken;
    @JsonProperty("jwks_uri")
    private String userinfoEndpoint;

}
