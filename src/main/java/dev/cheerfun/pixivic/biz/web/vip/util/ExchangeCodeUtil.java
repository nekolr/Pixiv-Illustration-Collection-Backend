package dev.cheerfun.pixivic.biz.web.vip.util;

import com.google.common.io.BaseEncoding;
import dev.cheerfun.pixivic.biz.web.vip.po.ExchangeCode;
import dev.cheerfun.pixivic.common.util.encrypt.CRC8;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static dev.cheerfun.pixivic.common.util.encrypt.ChaCha20.chacha20Decrypt;
import static dev.cheerfun.pixivic.common.util.encrypt.ChaCha20.chacha20Encrypt;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/13 10:44 AM
 * @description ExchangeCodeUtil
 */
@Component
public class ExchangeCodeUtil {
    private byte[] key;
    private byte[] nonce;
    private int counter;
    private BaseEncoding baseEncoding = BaseEncoding.base16();

    public ExchangeCodeUtil(@Value("${chacha20.key}") String key, @Value("${chacha20.nonce}") String nonce, @Value("${chacha20.counter}") int counter) {
        this.key = key.getBytes();
        this.nonce = nonce.getBytes();
        this.counter = counter;
    }

    public String generateExchangeCode(int exchangeCodeId, byte bizCode) {
        //构建payload 编号+业务数据  40位
        byte[] payload = ByteBuffer.allocate(5).putInt(exchangeCodeId).array();
        payload[4] = bizCode;
        System.out.println("最终payload为" + Arrays.toString(payload));
        //讲payload使用chacha20加密 得到40位密文
        byte[] ciphertext = chacha20Encrypt(payload, key, nonce, counter);
        System.out.println("最终密文为" + Arrays.toString(ciphertext));
        //使用crc8计算出密文的8位校验和
        byte checksum = CRC8.calcCrc8(ciphertext);
        System.out.println("校验和为" + checksum);
        //将密文拼接校验和 得到 48位结果
        byte[] result = new byte[ciphertext.length + 1];
        System.arraycopy(ciphertext, 0, result, 0, ciphertext.length);
        result[5] = checksum;
        System.out.println("最终结果为" + Arrays.toString(result));
        String encode = baseEncoding.encode(result);
        System.out.println("最终结果base16编码后为" + encode);
        return encode;
    }

    public ExchangeCode validateExchangeCode(String exchangeCode) {
        byte[] decode = baseEncoding.decode("exchangeCode");
        //首先校验 校验和
        byte c = CRC8.calcCrc8(decode, 0, 5);
        System.out.println("开始计算校验和：" + c);
        if (c == decode[5]) {
            //解密
            byte[] ciphertext = new byte[5];
            System.arraycopy(decode, 0, ciphertext, 0, ciphertext.length);
            byte[] payload = chacha20Decrypt(ciphertext, key, nonce, counter);
            //得到编号
            ByteBuffer wrapped = ByteBuffer.wrap(payload);
            return new ExchangeCode(wrapped.getInt(), payload[4]);
        }
        return null;
    }
}
