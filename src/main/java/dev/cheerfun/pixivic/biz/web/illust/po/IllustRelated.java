package dev.cheerfun.pixivic.biz.web.illust.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-1 下午8:39
 * @description IllustRelated
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IllustRelated {
    private int illustId;
    private int relatedIllustId;
    private int orderNum;
}
