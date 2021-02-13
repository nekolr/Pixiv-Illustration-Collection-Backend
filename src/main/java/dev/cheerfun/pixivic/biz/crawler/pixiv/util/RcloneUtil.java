package dev.cheerfun.pixivic.biz.crawler.pixiv.util;

import org.buildobjects.process.ProcBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/2/13 8:11 PM
 * @description RcloneUtil
 */
@Component
public class RcloneUtil {
    @Value("${rclone.path}")
    private String rclonePath;

    public void upload(String from, String to) {
        ProcBuilder.run(rclonePath, "copy", from, to);
    }
}
