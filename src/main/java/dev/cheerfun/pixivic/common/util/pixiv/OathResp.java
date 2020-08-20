package dev.cheerfun.pixivic.common.util.pixiv;

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
    @JsonSetter("access_token")
    private String accessToken;
    @JsonSetter("device_token")
    private String deviceToken;
    @JsonSetter("refresh_token")
    private String refreshToken;
}
