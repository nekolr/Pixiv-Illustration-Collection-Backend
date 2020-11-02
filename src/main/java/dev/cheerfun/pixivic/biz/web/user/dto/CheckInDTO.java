package dev.cheerfun.pixivic.biz.web.user.dto;

import dev.cheerfun.pixivic.biz.web.sentence.po.Sentence;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/11/2 8:05 PM
 * @description CheckInDTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInDTO {
    private Integer score;
    private Sentence sentence;
    private Illustration illustration;
}
