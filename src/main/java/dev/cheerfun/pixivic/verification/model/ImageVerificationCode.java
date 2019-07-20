package dev.cheerfun.pixivic.verification.model;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/14 13:11
 * @description 图片验证码
 */
public class ImageVerificationCode extends AbstractVerificationCode {
    private final static int WIDTH;
    private final static int HEIGHT;
    private final static int LINE_COUNT;
    private final static Random random;

    static {
        WIDTH = 200;
        HEIGHT = 60;
        LINE_COUNT = 40;
        random = new Random();
    }

    @Getter
    private String imageBase64;

    public ImageVerificationCode(String value) {
        super(value);
        this.value = value;
        char[] chars = value.toCharArray();
        int length = chars.length;
        int fontWidth = WIDTH / length;
        int fontHeight = HEIGHT - 5;
        int codeY = HEIGHT - 8;
        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        g.setColor(getRandColor(250, 255));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setFont(new Font("Arial", Font.BOLD, fontHeight));
        for (int i = 0; i < LINE_COUNT; i++) {
            int xs = random.nextInt(WIDTH);
            int ys = random.nextInt(HEIGHT);
            int xe = xs + random.nextInt(WIDTH);
            int ye = ys + random.nextInt(HEIGHT);
            g.setColor(getRandColor(200, 250));
            g.drawLine(xs, ys, xe, ye);
        }
        float yawpRate = 0.01f;
        int area = (int) (yawpRate * WIDTH * HEIGHT);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            bufferedImage.setRGB(x, y, random.nextInt(255));
        }

        for (int i = 0; i < length; i++) {
            String strRand = value.substring(i, i + 1);
            g.setColor(getRandColor(1, 255));
            g.drawString(strRand, i * fontWidth + 3, codeY);
        }
        g.dispose();
        ByteArrayOutputStream bs = null;
        try {
            bs = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", bs);
            imageBase64 = Base64.getEncoder().encodeToString(bs.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bs != null) {
                    bs.close();
                }
                bs = null;
                bufferedImage = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Color getRandColor(int fc, int bc) {// 给定范围获得随机颜色
        fc = fc > 255 ? 255 : fc;
        bc = bc > 255 ? 255 : bc;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b,188);
    }

}
