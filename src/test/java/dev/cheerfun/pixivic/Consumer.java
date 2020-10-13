package dev.cheerfun.pixivic;

import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import io.github.bucket4j.*;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static dev.cheerfun.pixivic.common.util.encrypt.ChaCha20.chacha20Decrypt;
import static dev.cheerfun.pixivic.common.util.encrypt.ChaCha20.chacha20Encrypt;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/16 3:01 下午
 * @description Consumer
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        int counter = 23;
        byte[] key = "生蚝是位兴趣使然的OTAKU".getBytes();
        byte[] nonce = "PIXIVICJUST4".getBytes();

        int a = 8888;//兑换码编号 32位int
        byte b = 127;//兑换码业务数据 8位
        System.out.println("换码编号为" + a);
        System.out.println("业务数据为" + b);
        //构建payload 编号+业务数据  40位
        byte[] payload = ByteBuffer.allocate(5).putInt(a).array();
        payload[4] = b;
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

        //使用base16对结果进行编码 得到12字符串
        BaseEncoding baseEncoding = BaseEncoding.base16();
        String encode = baseEncoding.encode(result);
        System.out.println("最终结果base16编码后为" + encode);

        byte[] decode = baseEncoding.decode("AF053CF856FC");
        //首先校验 校验和
        byte c = CRC8.calcCrc8(decode, 0, 5);
        System.out.println("开始计算校验和：" + c);
        if (c == decode[5]) {
            //解密
            byte[] temp = new byte[5];
            System.arraycopy(decode, 0, temp, 0, temp.length);
            byte[] bytes1 = chacha20Decrypt(temp, key, nonce, counter);
            //得到编号
            //检查编号是否被用过
            ByteBuffer wrapped = ByteBuffer.wrap(bytes1);
            System.out.println(wrapped.getInt());
            // System.out.println(intToBytes(bytes1));
            //得到业务数据
            System.out.println(bytes1[4]);

        }

    }

    public static byte[] intToBytes(int value) {

        byte[] src = new byte[4];

        src[3] = (byte) ((value >> 24) & 0xFF);

        src[2] = (byte) ((value >> 16) & 0xFF);

        src[1] = (byte) ((value >> 8) & 0xFF);

        src[0] = (byte) (value & 0xFF);

        return src;

    }

    public static byte[] hex2bytes(String hex) {
        hex = hex.replaceAll("[: ]", "");
        byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < result.length; i++) {
            String hexByte = hex.substring(i * 2, i * 2 + 2);
            result[i] = (byte) Integer.parseInt(hexByte, 16);
        }
        return result;
    }

}

