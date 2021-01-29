package dev.cheerfun.pixivic.biz.web.user.controller;

import dev.cheerfun.pixivic.basic.ratelimit.annotation.RateLimit;
import dev.cheerfun.pixivic.basic.verification.annotation.CheckVerification;
import dev.cheerfun.pixivic.basic.verification.domain.ImageVerificationCode;
import dev.cheerfun.pixivic.basic.verification.domain.PhoneMessageVerificationCode;
import dev.cheerfun.pixivic.biz.web.user.service.VerificationCodeService;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/20 9:47
 * @description 验证码控制器
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VerificationCodeController {
    private final VerificationCodeService verificationCodeService;

    @GetMapping("/verificationCode")
    public ResponseEntity<Result<ImageVerificationCode>> getImageVerificationCode() {
        ImageVerificationCode verificationCode = verificationCodeService.getImageVerificationCode();
        return ResponseEntity.ok().body(new Result<>("验证码获取成功", verificationCode));
    }

    @GetMapping("/messageVerificationCode")
    @CheckVerification
    @RateLimit
    public ResponseEntity<Result<PhoneMessageVerificationCode>> getMessageVerificationCode(@RequestParam("phone") String phone, @RequestParam("vid") String vid, @RequestParam("value") String value) {
        PhoneMessageVerificationCode verificationCode = verificationCodeService.getMessageVerificationCode(phone);
        return ResponseEntity.ok().body(new Result<>("短信验证码获取成功", verificationCode));
    }
}
