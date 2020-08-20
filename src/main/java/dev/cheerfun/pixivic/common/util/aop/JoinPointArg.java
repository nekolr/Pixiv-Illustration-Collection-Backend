package dev.cheerfun.pixivic.common.util.aop;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-8 下午7:34
 * @description JoinPointArg
 */
@Data
@AllArgsConstructor
public class JoinPointArg {
    private int index;
    private Object value;
}
