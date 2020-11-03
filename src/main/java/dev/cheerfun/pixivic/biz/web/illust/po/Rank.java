package dev.cheerfun.pixivic.biz.web.illust.po;

import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 17:41
 * @description Rank
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rank implements Serializable {
    private List<Illustration> data;
    private String mode;
    private String date;
}
