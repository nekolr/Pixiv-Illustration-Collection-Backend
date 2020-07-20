package dev.cheerfun.pixivic.biz.credit.po;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/20 2:59 下午
 * @description CreditConfig
 */
@Data
public class CreditConfig {
    private Integer id;
    private String objectType;
    private String action;
    private Integer limitNum;
    private Integer isRandom;
    private Integer score;
    private Integer randomStart;
    private Integer randomEnd;
    private String desc;
}
