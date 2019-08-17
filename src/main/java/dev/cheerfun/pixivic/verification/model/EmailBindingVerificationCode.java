package dev.cheerfun.pixivic.verification.model;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/14 13:38
 * @description 邮箱绑定验证码
 */
public class EmailBindingVerificationCode extends AbstractVerificationCode {
    private String email;

    public EmailBindingVerificationCode(String value, String email) {
        super(value);
        this.value=value+email;
        this.email=email;
    }

    @Override
    public String toString() {
        return value+email;
    }

}
