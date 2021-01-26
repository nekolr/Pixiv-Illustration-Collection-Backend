package dev.cheerfun.pixivic.biz.web.user.dto;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/26 3:16 PM
 * @description VerifiedDTO
 */
@Data
public class VerifiedResponseDTO {
    private String code;
    private String message;
    private VerifiedResponseResult result;
}
