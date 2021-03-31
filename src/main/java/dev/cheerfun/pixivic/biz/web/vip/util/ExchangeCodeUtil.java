package dev.cheerfun.pixivic.biz.web.vip.util;

import com.google.common.io.BaseEncoding;
import dev.cheerfun.pixivic.biz.web.vip.po.ExchangeCode;
import dev.cheerfun.pixivic.common.util.encrypt.CRC8;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
    private BaseEncoding baseEncoding = BaseEncoding.base32();
/*    @PostConstruct
    public void test(){
        System.out.println(validateExchangeCode("PMQJ6F7I3FGCC7XD"));
    }*/

    public ExchangeCodeUtil(@Value("${chacha20.key}") String key, @Value("${chacha20.nonce}") String nonce, @Value("${chacha20.counter}") int counter) {
        this.key = key.getBytes();
        this.nonce = nonce.getBytes();
        this.counter = counter;
    }

    public String generateExchangeCode(int exchangeCodeId, byte bizCode) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        byte[] result = new byte[10];
        //构建payload 随机数+编号+业务数据  72位
        byte[] payload = ByteBuffer.allocate(4).putInt(exchangeCodeId).array();
        byte[] randomInt = ByteBuffer.allocate(4).putInt(random.nextInt()).array();
        //将编号与随机数交叉存放
        byte[] payloadAndRandomInt = new byte[9];
        payloadAndRandomInt[0] = payload[0];
        payloadAndRandomInt[1] = randomInt[0];
        payloadAndRandomInt[2] = payload[1];
        payloadAndRandomInt[3] = randomInt[1];
        payloadAndRandomInt[4] = payload[2];
        payloadAndRandomInt[5] = randomInt[2];
        payloadAndRandomInt[6] = payload[3];
        payloadAndRandomInt[7] = randomInt[3];
        //最后一位是业务数据
        payloadAndRandomInt[8] = bizCode;
        //将payload使用chacha20加密 得到72位密文
        byte[] ciphertext = chacha20Encrypt(payloadAndRandomInt, key, nonce, counter);
        //使用crc8计算出密文的8位校验和
        byte checksum = CRC8.calcCrc8(ciphertext);
        //将密文拼接校验和 得到 80位结果
        System.arraycopy(ciphertext, 0, result, 0, ciphertext.length);
        result[9] = checksum;
        //base32编码返回
        return baseEncoding.encode(result);
    }

    public ExchangeCode validateExchangeCode(String exchangeCode) {
        byte[] decode = baseEncoding.decode(exchangeCode);
        //首先校验 校验和
        byte c = CRC8.calcCrc8(decode, 0, 9);
        if (c == decode[9]) {
            //解密
            byte[] ciphertext = new byte[9];
            System.arraycopy(decode, 0, ciphertext, 0, ciphertext.length);
            byte[] payload = chacha20Decrypt(ciphertext, key, nonce, counter);
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
