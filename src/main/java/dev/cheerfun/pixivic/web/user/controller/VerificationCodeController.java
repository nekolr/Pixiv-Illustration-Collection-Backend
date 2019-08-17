package dev.cheerfun.pixivic.web.user.controller;

import dev.cheerfun.pixivic.verification.model.ImageVerificationCode;
import dev.cheerfun.pixivic.web.common.model.Result;
import dev.cheerfun.pixivic.web.user.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
