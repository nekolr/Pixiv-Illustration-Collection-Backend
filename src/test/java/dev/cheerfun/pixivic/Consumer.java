package dev.cheerfun.pixivic;

import com.google.common.io.BaseEncoding;
import dev.cheerfun.pixivic.biz.web.vip.po.ExchangeCode;
import dev.cheerfun.pixivic.common.util.encrypt.CRC8;
import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import static dev.cheerfun.pixivic.common.util.encrypt.ChaCha20.chacha20Decrypt;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/16 3:01 下午
 * @description Consumer
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        System.out.println(1300000 * 8 * 4 / 1024 / 1024);

    }

    public static ExchangeCode validateExchangeCode(String exchangeCode) {
        byte[] decode = BaseEncoding.base32().decode(exchangeCode);
        //首先校验 校验和
        byte c = CRC8.calcCrc8(decode, 0, 9);
        if (c == decode[9]) {
            //解密
            byte[] ciphertext = new byte[9];
            System.arraycopy(decode, 0, ciphertext, 0, ciphertext.length);
            byte[] payload = chacha20Decrypt(ciphertext, "".getBytes(StandardCharsets.UTF_8), "".getBytes(StandardCharsets.UTF_8), 21);
            //得到编号
            byte[] codeId = new byte[4];
            codeId[0] = payload[0];
            codeId[1] = payload[2];
            codeId[2] = payload[4];
            codeId[3] = payload[6];
            ByteBuffer wrapped = ByteBuffer.wrap(codeId, 0, 4);
            return new ExchangeCode(wrapped.getInt(), payload[8]);
        }
        return null;
    }
}

@Data
class Predictions {
    Float[][] predictions;

}
