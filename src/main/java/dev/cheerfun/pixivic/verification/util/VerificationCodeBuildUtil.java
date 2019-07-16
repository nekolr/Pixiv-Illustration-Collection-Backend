package dev.cheerfun.pixivic.verification.util;

import dev.cheerfun.pixivic.verification.constant.VerificationType;
import dev.cheerfun.pixivic.verification.model.AbstractVerificationCode;
import dev.cheerfun.pixivic.verification.model.EmailBindingVerificationCode;
import dev.cheerfun.pixivic.verification.model.ImageVerificationCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/14 13:35
 * @description 验证码构建器
 */
@NoArgsConstructor
@Component
public class VerificationCodeBuildUtil {
    private final static Random random;

    static {
        random = new Random();
    }

    private static String generateRandomStr(int n) {
        String str1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder str2 = new StringBuilder();
        int len = str1.length() - 1;
        double r;
        for (int i = 0; i < n; i++) {
            r = (Math.random()) * len;
            str2.append(str1.charAt((int) r));
        }
        return str2.toString();
    }

    private String type;
    private String value;
    private String email;

    public static VerificationCodeBuildUtil create() {
        return new VerificationCodeBuildUtil();
    }

    public VerificationCodeBuildUtil setType(String type) {
        this.type = type;
        return this;
    }

    public VerificationCodeBuildUtil value(int n) {
        this.value = generateRandomStr(n);
        return this;
    }

    public VerificationCodeBuildUtil value(String value) {
        this.value = value;
        return this;
    }

    public VerificationCodeBuildUtil email(String email) {
        this.value = value;
        return this;
    }

    public AbstractVerificationCode build() {
        switch (this.type) {
            case VerificationType.IMG:
                return new ImageVerificationCode(value);
            case VerificationType.EMAIL_CHECK:
            case VerificationType.PASSWORD_FORGET:
                return new EmailBindingVerificationCode(value, email);
            default:
                return null;
        }
    }

}
