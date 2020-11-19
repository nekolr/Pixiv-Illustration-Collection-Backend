package dev.cheerfun.pixivic;

import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import io.github.bucket4j.*;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.web.bind.annotation.RequestHeader;

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
        String xForwardedFor = "218.24.120.112";
        int i = xForwardedFor.indexOf(",");
        if (i == -1) {
            i = xForwardedFor.length();
        }
        System.out.println(xForwardedFor.substring(0, i));
        int ip = ip2Int(xForwardedFor.substring(0, i));
        System.out.println(ip);
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

    public static int ip2Int(String ipString) {
        // 取 ip 的各段
        String[] ipSlices = ipString.split("\\.");
        int rs = 0;
        for (int i = 0; i < ipSlices.length; i++) {
            // 将 ip 的每一段解析为 int，并根据位置左移 8 位
            int intSlice = Integer.parseInt(ipSlices[i]) << 8 * i;
            // 或运算
            rs = rs | intSlice;
        }
        return rs;
    }
}

