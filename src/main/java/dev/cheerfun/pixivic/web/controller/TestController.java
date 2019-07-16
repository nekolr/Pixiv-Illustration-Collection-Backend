package dev.cheerfun.pixivic.web.controller;

import dev.cheerfun.pixivic.auth.constant.AuthLevel;
import dev.cheerfun.pixivic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.auth.annotation.AuthRequired;
import dev.cheerfun.pixivic.common.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/16 9:21
 * @description TODO
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@AuthRequired(AuthLevel.ANONYMOUS)
public class TestController {
    private final JWTUtil jwtUtil;

    @AuthRequired(AuthLevel.VIP)
    @GetMapping("/auth")
    public String test(@RequestHeader("Authorization") String token) {
        return "233";
    }
    @GetMapping("/token")
    public String login() {
        User user = new User();
        user.setAvatar("233");
        user.setUsername("233");
        user.setLevel(1);
        return jwtUtil.getToken(user);
    }
}
