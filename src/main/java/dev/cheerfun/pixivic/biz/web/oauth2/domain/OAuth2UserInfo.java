package dev.cheerfun.pixivic.biz.web.oauth2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/4 6:47 PM
 * @description OAuth2UserInfo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2UserInfo {
    private Integer id;
    private String name;
    private String email;
}
