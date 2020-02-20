package dev.cheerfun.pixivic.basic.ad.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/20 下午4:26
 * @description Advertisement
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Advertisement extends Illustration {

}
