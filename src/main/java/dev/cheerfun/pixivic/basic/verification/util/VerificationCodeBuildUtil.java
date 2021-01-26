package dev.cheerfun.pixivic.basic.verification.util;

import dev.cheerfun.pixivic.basic.verification.constant.VerificationType;
import dev.cheerfun.pixivic.basic.verification.domain.AbstractVerificationCode;
import dev.cheerfun.pixivic.basic.verification.domain.EmailBindingVerificationCode;
import dev.cheerfun.pixivic.basic.verification.domain.ImageVerificationCode;
import dev.cheerfun.pixivic.basic.verification.domain.PhoneMessageVerificationCode;

import java.util.Random;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/14 13:35
 * @description 验证码构建器
 */

public class VerificationCodeBuildUtil {
    private final static String str;
    private final static String num_str;

    static {
        str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        num_str = "1234567890";
    }

    private String type;
    private String value;
    private String email;
    private String phone;

    private VerificationCodeBuildUtil(String type) {
        this.type = type;
    }

    private static String generateRandomStr(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        int len = str.length() - 1;
        double r;
        for (int i = 0; i < n; i++) {
            r = (Math.random()) * len;
            stringBuilder.append(str.charAt((int) r));
        }
        return stringBuilder.toString();
    }

    private static String generateRandomNumberStr(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        int len = num_str.length() - 1;
        double r;
        for (int i = 0; i < n; i++) {
            r = (Math.random()) * len;
            stringBuilder.append(num_str.charAt((int) r));
        }
        return stringBuilder.toString();
    }

    public static VerificationCodeBuildUtil create(String type) {
        return new VerificationCodeBuildUtil(type);
    }

    public VerificationCodeBuildUtil type(String type) {
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
        this.email = email;
        return this;
    }

    public AbstractVerificationCode build() throws RuntimeException {
        if (type == null) {
            throw new RuntimeException("未指定类型");
        }
        switch (type) {
            case VerificationType.IMG:
                return new ImageVerificationCode(generateRandomStr(4));
            case VerificationType.EMAIL_CHECK:
                return new EmailBindingVerificationCode(generateRandomStr(4), email);
            case VerificationType.MESSAGE:
                return new PhoneMessageVerificationCode(generateRandomNumberStr(6), phone);
            default:
                return null;
        }
    }

}
