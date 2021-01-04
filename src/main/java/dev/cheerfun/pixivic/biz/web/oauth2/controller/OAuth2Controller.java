package dev.cheerfun.pixivic.biz.web.oauth2.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.biz.web.oauth2.config.OauthAuthorizationServer;
import dev.cheerfun.pixivic.biz.web.oauth2.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/4 9:25 AM
 * @description OAuth2Controller
 */
@RestController

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;

    @GetMapping("/.well-known/oauth-authorization-server")
    @ResponseBody
    public ResponseEntity<OauthAuthorizationServer> serverMetadataInfo() {
        return ResponseEntity.ok().body(oAuth2Service.queryServerInfo());
    }

    @GetMapping("/oauth/authorize")
    @PermissionRequired
    public ResponseEntity<Object> authorize(@RequestParam(name = "referer", required = false) String referer,
                                            @RequestParam(value = "client_id") Integer clientId,
                                            @RequestParam(value = "response_type", required = false) String responseType,
                                            @RequestParam(value = "state", required = false) String state,
                                            @RequestParam(value = "scope", required = false) String scope,
                                            @RequestParam(value = "user_oauth_approval", required = false, defaultValue = "false") boolean userOauthApproval,
                                            @RequestParam(value = "redirect_uri") String redirectUri,
                                            @RequestHeader(value = "Authorization") String token) {
        String result = oAuth2Service.authorize(clientId, state, redirectUri);
        System.out.println(result);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header("Location", result).header("Cache-Control", "no-cache").body(null);
    }

    @PostMapping("/oauth/token")
    @GetMapping("/oauth/token")
    public ResponseEntity<Object> token(
            @RequestParam(value = "client_id") Integer clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "refresh_token", required = false) String refreshToken,
            @RequestParam(value = "grant_type") String grantType,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "redirect_uri") String redirectUri) {
        return ResponseEntity.ok().body(oAuth2Service.token(clientId, clientSecret, grantType, refreshToken, code, redirectUri));
    }

    @GetMapping("/oauth/me")
    public ResponseEntity<Object> userinfo(@RequestHeader(value = "authorization") String accessToken) {
        return ResponseEntity.ok().body(oAuth2Service.userinfo(accessToken));
    }

}
