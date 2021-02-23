package dev.cheerfun.pixivic.biz.crawler.pixiv.service;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import dev.cheerfun.pixivic.biz.crawler.pixiv.util.RcloneUtil;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/2/12 7:46 PM
 * @description ImageService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImageService {
    private LinkedBlockingQueue<Illustration> waitForUploadQueue;
    private final ExecutorService crawlerExecutorService;
    private final HttpClient httpClient;
    private final RcloneUtil rcloneUtil;
    private final OkHttpClient client = new OkHttpClient();
    @Value("${pixiv.imageSavePath}")
    private String imageSavePath;

    @PostConstruct
    public void init() {

        //初始化50个爬取线程与40个上传线程
        //设置一个同步的id变量
        //每个下载线程不停的每次取二十个画作然后修改变量
        //取个画作
        //进行

    }

    public void downloadProcess(Illustration illustration) {

        illustration.getImageUrls().forEach(e -> {
            download(e.getSquareMedium());
            download(e.getMedium());
            download(e.getLarge());
            download(e.getLarge());
        });

        waitForUploadQueue.offer(illustration);

    }

    public void uploadProcess(Illustration illustration) {

        illustration.getImageUrls().forEach(e -> {
            try {

                URL sm = new URL(e.getSquareMedium());
                URL m = new URL(e.getMedium());
                URL l = new URL(e.getLarge());
                URL o = new URL(e.getOriginal());
                upload(Path.of(imageSavePath, sm.getFile()).toString(), null);
                upload(Path.of(imageSavePath, m.getFile()).toString(), null);
                upload(Path.of(imageSavePath, l.getFile()).toString(), null);
                upload(Path.of(imageSavePath, o.getFile()).toString(), null);
                //删除本地文件
                Files.delete(Path.of(imageSavePath, sm.getFile()));
                Files.delete(Path.of(imageSavePath, m.getFile()));
                Files.delete(Path.of(imageSavePath, l.getFile()));
                Files.delete(Path.of(imageSavePath, o.getFile()));
                //更新数据库中记录

            } catch (IOException malformedURLException) {
                malformedURLException.printStackTrace();
            }
        });

    }

    //下载工具方法
    public Boolean download(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .addHeader("referer", "https://pixiv.net")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36")
                .url(url)
                .build();
        Call call = client.newCall(request);
        InputStream is = null;
        try {
            Response response = call.execute();
            is = response.body().byteStream();
            // 将流写入到文件
            Path path = Path.of(imageSavePath, url.getFile());
            Files.createDirectories(path.getParent());
            Files.write(path, is.readAllBytes(), StandardOpenOption.CREATE);
            //将
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
        return true;
    }

    //上传工具方法
    public void upload(String from, String to) {
        rcloneUtil.upload(from, "bak:/");
    }

}
