package dev.cheerfun.pixivic.biz.web.user.util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 9:48
 * @description PasswordUtil
 */
@Component
public class PasswordUtil {
    public String encrypt(String password) {
        byte[] plainText = password.getBytes();
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.reset();
            md.update(plainText);
            byte[] encodedPassword = md.digest();
            for (int i = 0; i < encodedPassword.length; i++) {
                if ((encodedPassword[i] & 0xff) < 0x10) {
                    sb.append("0");
                }

                sb.append(Long.toString(encodedPassword[i] & 0xff, 16));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
