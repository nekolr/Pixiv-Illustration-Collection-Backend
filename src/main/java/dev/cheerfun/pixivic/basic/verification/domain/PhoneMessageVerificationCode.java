package dev.cheerfun.pixivic.basic.verification.domain;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/14 13:38
 * @description 邮箱绑定验证码
 */
public class PhoneMessageVerificationCode extends AbstractVerificationCode {

    public PhoneMessageVerificationCode(String value, String vid) {
        super.vid = vid;
        super.value = value;
    }

    @Override
    public String toString() {
        return vid;
    }
}
