package dev.cheerfun.pixivic.web.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import dev.cheerfun.pixivic.infrastructure.cache.CacheUtils;
import dev.cheerfun.pixivic.infrastructure.constant.PixConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 验证码生成
 *
 * @author huangsm
 */
@RestController
@RequestMapping("/authCode")
public class AuthCodeController {
    @Resource(name = "defaultKaptcha")
    private DefaultKaptcha captchaProducer;


    /**
     * 验证码生成器
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("/{username}")
    public void generate(@PathVariable("username") String username, HttpServletResponse response) throws IOException {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        // 生成验证码
        String code = captchaProducer.createText();
        CacheUtils.put(PixConstant.KEY_CAPTCHA_GENERATE + username, code);

        // 向客户端写出
        BufferedImage bi = captchaProducer.createImage(code);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }
}
