package dev.cheerfun.pixivic.web.controller;

import dev.cheerfun.pixivic.verification.constant.VerificationType;
import dev.cheerfun.pixivic.verification.model.AbstractVerificationCode;
import dev.cheerfun.pixivic.verification.model.ImageVerificationCode;
import dev.cheerfun.pixivic.verification.util.VerificationCodeBuildUtil;
import dev.cheerfun.pixivic.web.constant.StatusCode;
import dev.cheerfun.pixivic.web.model.Result;
import dev.cheerfun.pixivic.web.service.VerificationCodeService;
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
    @GetMapping("/verification-code")
    public ResponseEntity<Result<ImageVerificationCode>> getVerificationCode() {
        ImageVerificationCode verificationCode = verificationCodeService.getVerificationCode();
        return ResponseEntity.ok().body(new Result<>(StatusCode.SUCCESS, verificationCode));
    }
}
