package dev.cheerfun.pixivic.biz.web.user.service;

import dev.cheerfun.pixivic.basic.verification.constant.VerificationType;
import dev.cheerfun.pixivic.basic.verification.domain.EmailBindingVerificationCode;
import dev.cheerfun.pixivic.basic.verification.domain.ImageVerificationCode;
import dev.cheerfun.pixivic.basic.verification.domain.PhoneMessageVerificationCode;
import dev.cheerfun.pixivic.basic.verification.util.VerificationCodeBuildUtil;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/20 10:56
 * @description VerificationCodeService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VerificationCodeService {
    private final StringRedisTemplate stringRedisTemplate;

    public PhoneMessageVerificationCode getMessageVerificationCode() {
        PhoneMessageVerificationCode verificationCode = (PhoneMessageVerificationCode) VerificationCodeBuildUtil.create(VerificationType.MESSAGE).build();
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.VERIFICATION_CODE + verificationCode.getVid(), verificationCode.getValue(), 3, TimeUnit.MINUTES);
        //调用发送短信api

        return verificationCode;
    }

    public ImageVerificationCode getImageVerificationCode() {
        ImageVerificationCode verificationCode = (ImageVerificationCode) VerificationCodeBuildUtil.create(VerificationType.IMG).build();
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.VERIFICATION_CODE + verificationCode.getVid(), verificationCode.getValue(), 10, TimeUnit.MINUTES);
        return verificationCode;
    }

    public EmailBindingVerificationCode getEmailVerificationCode(String email) {
        EmailBindingVerificationCode verificationCode = (EmailBindingVerificationCode) VerificationCodeBuildUtil
                .create(VerificationType.EMAIL_CHECK)
                .email(email).build();
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.VERIFICATION_CODE + verificationCode.getVid(), verificationCode.getValue(), 3, TimeUnit.HOURS);
        return verificationCode;
    }
}
