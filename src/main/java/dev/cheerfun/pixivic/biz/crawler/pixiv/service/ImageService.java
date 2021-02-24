package dev.cheerfun.pixivic.biz.crawler.pixiv.service;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import dev.cheerfun.pixivic.biz.crawler.pixiv.secmapper.ImageMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.util.RcloneUtil;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/2/12 7:46 PM
 * @description ImageService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImageService {
    private LinkedBlockingQueue<Illustration> waitForUploadQueue;
    private final ExecutorService crawlerExecutorService;
    private final IllustrationBizService illustrationBizService;
    private final HttpClient httpClient;
    private final RcloneUtil rcloneUtil;
    private final ImageMapper imageMapper;
    private final OkHttpClient client = new OkHttpClient();
    @Value("${pixiv.imageSavePath}")
    private String imageSavePath;

    private int illustIdFlag;

    @PostConstruct
    public void init() {
        //初始化flag
        log.info("开始进行图片持久化任务");
        illustIdFlag = imageMapper.queryLatest();
        waitForUploadQueue = new LinkedBlockingQueue<>(10000000);
        //初始化50个爬取线程与40个上传线程
        for (int i = 0; i < 30; i++) {
            crawlerExecutorService.submit(() -> {
                while (true) {
                    List<Integer> queryIllustIdList = queryIllustIdList();
                    List<Illustration> illustrations = illustrationBizService.queryIllustrationByIdList(queryIllustIdList);
                    illustrations.forEach(this::downloadProcess);
                }
            });
        }

        for (int i = 0; i < 10; i++) {
            crawlerExecutorService.submit(() -> {
                while (true) {
                    Illustration illustration = waitForUploadQueue.take();
                    uploadProcess(illustration);

                }
            });
        }

    }

    public synchronized List<Integer> queryIllustIdList() {
        //获取50个画作
        List<Integer> illustIdList = imageMapper.queryIllustIdList(illustIdFlag);
        //修改illustIdFlag
        illustIdFlag = illustIdList.get(illustIdList.size() - 1);
        return illustIdList;
    }

    public void downloadProcess(Illustration illustration) {
        log.info("画作：" + illustration.getId() + "开始下载");
        illustration.getImageUrls().forEach(e -> {
            download(e.getSquareMedium());
            download(e.getMedium());
            download(e.getLarge());
            download(e.getOriginal());
        });
        log.info("画作：" + illustration.getId() + "下载完毕");
        waitForUploadQueue.offer(illustration);

    }

    public void uploadProcess(Illustration illustration) {
        log.info("画作：" + illustration.getId() + "开始上传");
        illustration.getImageUrls().forEach(e -> {
            try {
                URL sm = new URL(e.getSquareMedium());
                URL m = new URL(e.getMedium());
                URL l = new URL(e.getLarge());
                URL o = new URL(e.getOriginal());
                if (upload(Path.of(imageSavePath, sm.getFile()).toString(), sm.getFile()) && upload(Path.of(imageSavePath, m.getFile()).toString(), m.getFile()) && upload(Path.of(imageSavePath, l.getFile()).toString(), l.getFile()) && upload(Path.of(imageSavePath, o.getFile()).toString(), o.getFile())) {
                    Files.delete(Path.of(imageSavePath, sm.getFile()));
                    Files.delete(Path.of(imageSavePath, m.getFile()));
                    Files.delete(Path.of(imageSavePath, l.getFile()));
                    Files.delete(Path.of(imageSavePath, o.getFile()));
                    imageMapper.updateImageSync(illustration.getId());
                }
                log.info("画作：" + illustration.getId() + "上传完毕");
                //删除本地文件
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
    public Boolean upload(String from, String to) {

        return rcloneUtil.upload(from, "bak:" + to.substring(0, to.lastIndexOf("/")));
    }

}
