package dev.cheerfun.pixivic.basic.verification.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/14 13:01
 * @description 验证码抽象类
 */
@NoArgsConstructor
public abstract class AbstractVerificationCode {
    @Getter
    protected String vid;
    @Getter
    @JsonIgnore
    protected String value;

    protected AbstractVerificationCode(String value) {
        this.vid = UUID.randomUUID().toString();
        this.value = value;
    }

}
