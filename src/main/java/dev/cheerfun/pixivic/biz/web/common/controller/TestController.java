package dev.cheerfun.pixivic.biz.web.common.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.basic.ratelimit.annotation.RateLimit;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.basic.verification.annotation.CheckVerification;
import dev.cheerfun.pixivic.biz.analysis.tag.service.TrendingTagsService;
import dev.cheerfun.pixivic.biz.crawler.news.service.NewService;
import dev.cheerfun.pixivic.biz.crawler.pixiv.mapper.IllustrationMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.service.ArtistService;
import dev.cheerfun.pixivic.biz.crawler.pixiv.service.IllustRankService;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.user.dto.SignUpDTO;
import dev.cheerfun.pixivic.biz.web.user.service.BusinessService;
import dev.cheerfun.pixivic.common.po.Result;
import dev.cheerfun.pixivic.common.util.EmailUtil;
import dev.cheerfun.pixivic.common.util.pixiv.OauthManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

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
    private final OauthManager oauthManager;
    private final StringRedisTemplate stringRedisTemplate;
    private final IllustRankService rankDailyService;
    private final EmailUtil emailUtil;

    private final TrendingTagsService trendingTagsService;
    private final ArtistService artistService;
    private final IllustrationMapper illustrationMapper;
    private final BusinessService businessService;
    private final NewService newService;

    //@PermissionRequired(PermissionLevel.VIP)
    @PostMapping("/test")
    @RateLimit
    //@PermissionRequired
    public ResponseEntity<String> test(@RequestBody @SensitiveCheck SignUpDTO signUpDTO,/*@RequestHeader("Authorization")  String token,*/@RequestParam @SensitiveCheck String content, @RequestParam @SensitiveCheck String title) throws InterruptedException, ExecutionException, IOException {
        trendingTagsService.dailyTask();
        return ResponseEntity.ok().body("");
    }

    @GetMapping("/test")
    //@PermissionRequired
    public ResponseEntity<String> test(@RequestParam String date) throws InterruptedException, ExecutionException, IOException, MessagingException {
        //spotlightService.deal();
        //businessService.follow(53,123);
        //emailUtil.sendEmail("392822872@qq.com","sasa","sasa","sas0","");
        trendingTagsService.dailyTask(LocalDate.parse(date));
        return ResponseEntity.ok().body("content");
    }
    /*@GetMapping("/32")
    public String login() throws InterruptedException {
        oauthUtil.getOauths().forEach(o -> System.out.println(o.getAccessToken()));
        illustrationPersistentService.dailyPersistentTask();
        User user = new User();
        user.setAvatar("233");
        user.setUsername("233");
        user.setLevel(1);
        AbstractVerificationCode code = VerificationCodeBuildUtil.create(VerificationType.IMG).build();
        AbstractVerificationCode code2 = VerificationCodeBuildUtil.create(VerificationType.EMAIL_CHECK).email("392822872").build();
           stringRedisTemplate.opsForValue().set(code.getVid(), code.getValue());

        stringRedisTemplate.opsForValue().set(code2.getVid(), code2.getValue());
        System.out.println(code2.getValue().equals(stringRedisTemplate.opsForValue().get(code2.getVid())));

        return null;
    }*/

    @PermissionRequired(PermissionLevel.VIP)
    @PostMapping("/auth")
    public String testAOP(@RequestBody() User user, @RequestHeader("Authorization") String token) {
        return "233";
    }

    @PostMapping("/testCode")
    @CheckVerification
    public ResponseEntity<Result> testCode(@RequestParam("vid") String vid, @RequestParam("value") String value, @RequestBody User user) {
        System.out.println(user);
        return ResponseEntity.ok(new Result<>("测试", null));
    }
}
