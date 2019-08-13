package dev.cheerfun.pixivic.web.controller;

import dev.cheerfun.pixivic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.common.constant.StatusCode;
import dev.cheerfun.pixivic.common.model.User;
import dev.cheerfun.pixivic.crawler.service.IllustrationPersistentService;
import dev.cheerfun.pixivic.crawler.util.OauthUtil;
import dev.cheerfun.pixivic.verification.annotation.CheckVerification;
import dev.cheerfun.pixivic.verification.constant.VerificationType;
import dev.cheerfun.pixivic.verification.model.AbstractVerificationCode;
import dev.cheerfun.pixivic.verification.util.VerificationCodeBuildUtil;
import dev.cheerfun.pixivic.web.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/16 9:21
 * @description TODO
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {
    private final JWTUtil jwtUtil;
    private final OauthUtil oauthUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final IllustrationPersistentService illustrationPersistentService;

    @PermissionRequired(PermissionLevel.VIP)
    @GetMapping("/auth")
    public String test(@RequestHeader("Authorization") String token) {
        return "233";
    }

    @GetMapping("/32")
    public String login() throws InterruptedException {
        oauthUtil.getOauths().forEach(o -> System.out.println(o.getAccess_token()));
        illustrationPersistentService.dailyPersistentTask();
        User user = new User();
        user.setAvatar("233");
        user.setUsername("233");
        user.setLevel(1);
        AbstractVerificationCode code = VerificationCodeBuildUtil.create(VerificationType.IMG).build();
        AbstractVerificationCode code2 = VerificationCodeBuildUtil.create(VerificationType.EMAIL_CHECK).email("392822872").build();
        //   stringRedisTemplate.opsForValue().set(code.getVid(), code.getValue());

        stringRedisTemplate.opsForValue().set(code2.getVid(), code2.getValue());
        System.out.println(code2.getValue().equals(stringRedisTemplate.opsForValue().get(code2.getVid())));

        return jwtUtil.getToken(user);
    }

    @PermissionRequired(PermissionLevel.VIP)
    @PostMapping("/auth")
    public String testAOP(@RequestBody() User user, @RequestHeader("Authorization") String token) {
        return "233";
    }

    @PostMapping("/testCode")
    @CheckVerification
    public ResponseEntity<Result> testCode(@RequestParam("vid") String vid, @RequestParam("value") String value, @RequestBody User user) {
        System.out.println(user);
        return ResponseEntity.ok(new Result<>(StatusCode.SUCCESS,null));
    }
}
