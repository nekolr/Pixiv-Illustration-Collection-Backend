package dev.cheerfun.pixivic.basic.verification.domain;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/14 13:38
 * @description 邮箱绑定验证码
 */
public class PhoneMessageVerificationCode extends AbstractVerificationCode {
    private String phone;

    public PhoneMessageVerificationCode(String value, String email) {
        super(value);
        this.value = value + ":" + phone;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return value + phone;
    }

}
