package dev.cheerfun.pixivic.web.controller;

import dev.cheerfun.pixivic.common.constant.StatusCode;
import dev.cheerfun.pixivic.common.model.QQOAuth2;
import dev.cheerfun.pixivic.common.model.User;
import dev.cheerfun.pixivic.verification.annotation.CheckVerification;
import dev.cheerfun.pixivic.web.model.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/21 17:22
 * @description UserController
 */
@RestController
public class UserController {
    @PostMapping("/user")
    @CheckVerification
    public ResponseEntity<Result> userRegister(@RequestParam("vid") String vid, @RequestParam("value") String value, @RequestBody User user) {
        System.out.println(user);
        return ResponseEntity.ok(new Result<>(StatusCode.SUCCESS, null));
    }

    @PostMapping("/user/{uid}/QQAuth")
    public ResponseEntity<Result> userBindQQ(@PathVariable String uid, @RequestBody QQOAuth2 qqoAuth2, @RequestHeader("Authorization") String token) {
        System.out.println(qqoAuth2);
        return ResponseEntity.ok(new Result<>(StatusCode.SUCCESS, null));
    }

    @DeleteMapping("/user/{uid}/QQAuth")
    public ResponseEntity<Result> userUnBindQQ(@PathVariable String uid, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(new Result<>(StatusCode.SUCCESS, null));
    }

    @GetMapping("/token")
    public ResponseEntity<Result> userLogin(@RequestBody Map<String, String> loginInfo) {
        System.out.println(loginInfo);
       // return ResponseEntity.ok().header("Authorization",);
        return null;
    }



}
