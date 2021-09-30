package dev.cheerfun.pixivic.common.util.pixiv;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/1/17 上午9:27
 * @description OathResp
 */
@Data
public class OathResp {
    @JsonAlias({"access_token", "accessToken"})
    private String accessToken;
    @JsonAlias({"device_token", "deviceToken"})
    private String deviceToken;
    @JsonAlias({"refresh_token", "refreshToken"})
    private String refreshToken;
}
