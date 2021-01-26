package dev.cheerfun.pixivic.biz.web.user.dto;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/26 3:32 PM
 * @description VerifiedResponseResult
 */
@Data
public class VerifiedResponseResult {
    private String name;
    private String mobile;
    private String idcard;
    private String res;
    private String description;
    private String sex;
    private String birthday;
    private String address;
}
