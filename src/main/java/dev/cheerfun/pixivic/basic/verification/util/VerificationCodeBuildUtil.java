package dev.cheerfun.pixivic.basic.verification.util;

import dev.cheerfun.pixivic.basic.verification.constant.VerificationType;
import dev.cheerfun.pixivic.basic.verification.domain.AbstractVerificationCode;
import dev.cheerfun.pixivic.basic.verification.domain.EmailBindingVerificationCode;
import dev.cheerfun.pixivic.basic.verification.domain.ImageVerificationCode;

import java.util.Random;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/14 13:35
 * @description 验证码构建器
 */

public class VerificationCodeBuildUtil {
    private final static Random random;
    private final static String str;

    private VerificationCodeBuildUtil(String type) {
        this.type = type;
    }

    static {
        random = new Random();
        str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
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

    private String type;
    private String value;
    private String email;

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
        if (value == null) {
            value = generateRandomStr(4);
        }
        switch (type) {
            case VerificationType.IMG:
                return new ImageVerificationCode(value);
            case VerificationType.EMAIL_CHECK:
                return new EmailBindingVerificationCode(value, email);
            default:
                return null;
        }
    }

}
