package dev.cheerfun.pixivic.verification.model;

import lombok.Getter;

import java.util.UUID;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/14 13:01
 * @description 验证码抽象类
 */
public abstract class AbstractVerificationCode {
    @Getter
    protected String vid;
    @Getter
    protected String value;

    public AbstractVerificationCode(String value) {
        this.vid = UUID.randomUUID().toString();
        this.value = value;
    }

}
