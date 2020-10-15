package dev.cheerfun.pixivic.biz.web.reward.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/15 7:06 PM
 * @description Reward
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reward {
    private Integer id;
    private String appType;
    private Integer appId;
    private Integer from;
    private String fromName;
    private Integer to;
    private Integer price;
    private LocalDateTime createDate;

    public void init(int userId) {
        id = null;
        from = userId;
        createDate = LocalDateTime.now();
    }
}
