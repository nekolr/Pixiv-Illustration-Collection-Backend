package dev.cheerfun.pixivic.biz.web.vip.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/13 12:26 PM
 * @description ExchangeCode
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeCode {
    private int id;
    private byte type;
    private String createDate;
    private int useFlag;

    public ExchangeCode(int id, byte type) {
        this.id = id;
        this.type = type;
    }

    public ExchangeCode(int id, byte type, int useFlag) {
        this.id = id;
        this.type = type;
        this.useFlag = useFlag;
    }
}
