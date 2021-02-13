package dev.cheerfun.pixivic;

import com.google.common.io.BaseEncoding;
import com.squareup.okhttp.*;
import dev.cheerfun.pixivic.biz.web.vip.po.ExchangeCode;
import dev.cheerfun.pixivic.common.util.encrypt.CRC8;
import lombok.Data;
import org.buildobjects.process.ProcBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import static dev.cheerfun.pixivic.common.util.encrypt.ChaCha20.chacha20Decrypt;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/16 3:01 下午
 * @description Consumer
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        download("https://i.pximg.net/img-original/img/2021/02/09/01/12/48/87632329_p1.png", "/Users/oysterqaq/Desktop/");

        System.out.println();
    }

    public static void download(String url, String savePath) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("referer", "https://pixiv.net")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36")
                .url(url)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        URL u = new URL(url);
        InputStream is = null;
        try {
            is = response.body().byteStream();
            // 将流写入到文件
            Path path = Path.of(savePath, u.getFile());
            Files.createDirectories(path.getParent());
            Files.write(path, is.readAllBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
