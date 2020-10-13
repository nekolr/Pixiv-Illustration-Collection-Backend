package dev.cheerfun.pixivic.common.util.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/13 10:42 AM
 * @description ChaCha20
 */
public class ChaCha20 {
    public static byte[] chacha20Encrypt(byte[] message, byte[] key, byte[] nonce, int counter) {
        return chacha20(message, key, nonce, counter, Cipher.ENCRYPT_MODE);
    }

    public static byte[] chacha20Decrypt(byte[] message, byte[] key, byte[] nonce, int counter) {
        return chacha20(message, key, nonce, counter, Cipher.DECRYPT_MODE);
    }

    private static byte[] chacha20(byte[] message, byte[] key, byte[] nonce, int counter, int mode) {
        // nonce 长度必须为 12字节
        if (nonce == null || nonce.length != 12) {
            throw new IllegalArgumentException("nonce must be 12 bytes in length");
        }
        // 密钥的长度必须为256位，即32字节
        if (key == null || key.length != 32) {
            throw new IllegalArgumentException("key length must be 256 bits");
        }
        Key theKey = new SecretKeySpec(key, "ChaCha20");
        ChaCha20ParameterSpec spec = new ChaCha20ParameterSpec(nonce, counter);
        try {
            Cipher cipher = Cipher.getInstance("ChaCha20");
            cipher.init(mode, theKey, spec);
            return cipher.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
