package dev.cheerfun.pixivic.biz.crawler.pixiv.util;

import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.TimeoutException;
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

    public Boolean upload(String from, String to) {
      /*  String cmd = "ls -al";
        Runtime run = Runtime.getRuntime();
        try {
            Process pr = run.exec(rclonePath+" copy "+from+" "+to);
            pr.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        ProcBuilder builder = new ProcBuilder(rclonePath)
                .withArg("copy")
                .withArg(from)
                .withArg(to)
                .withTimeoutMillis(1000 * 60 * 3);
        try {
            builder.run();
            return true;
        } catch (TimeoutException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        // ProcBuilder.run(rclonePath, "copy", from, to);
    }
}
