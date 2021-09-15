package dev.cheerfun.pixivic.biz.sitemap.service;

import com.thoughtworks.xstream.XStream;
import dev.cheerfun.pixivic.biz.sitemap.constant.SiteMapConstant;
import dev.cheerfun.pixivic.biz.sitemap.po.SiteMap;
import dev.cheerfun.pixivic.biz.sitemap.po.SiteMapIndex;
import dev.cheerfun.pixivic.biz.sitemap.po.Url;
import dev.cheerfun.pixivic.biz.sitemap.po.UrlSet;
import dev.cheerfun.pixivic.biz.web.illust.secmapper.IllustrationBizMapper;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.util.ssh.SshUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/6/28 2:20 下午
 * @description SiteMapService
 */
//@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SiteMapService {
    private final SshUtil sshUtil;
    private final XStream xStream;
    private final IllustrationBizMapper illustrationBizMapper;
    @Value("${sitemap.savePath}")
    private String siteMapSavePath;
    @Value("${sitemap.remoteSavePath}")
    private String siteMapRemoteSavePath;
    private final static String[] MODES = {"day", "week", "month", "day_female", "day_male", "day_manga", "week_manga", "month_manga", "week_rookie_manga"};
    private final static SimpleDateFormat UTC_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    //@Scheduled
    public void dailyTask() throws IOException {
        dealRank();
        dealIllust();
        reGenerateSiteMap();
    }

    //@PostConstruct
    public void test() throws IOException {
        dealIllust();
        //dealRank();
        reGenerateSiteMap();
        sshUtil.disconnect(SiteMapConstant.SITEMAP_WEB_HOST);
    }

    private void reGenerateSiteMap() throws IOException {
        //重新生成sitemap.xml
        String fileName = siteMapSavePath + SiteMapConstant.SITEMAP_NAME;
        //读取本地目录
        List<SiteMap> siteMapList = Files.list(Paths.get(siteMapSavePath))
                .filter(e -> Files.isRegularFile(e) && !SiteMapConstant.SITEMAP_NAME.equals(e.getFileName().toString()))
                .map(e -> {
                    return new SiteMap(SiteMapConstant.SITEMAP_WEB_CONTENT + e.getFileName().toString(), UTC_FORMAT.format(new Date(e.toFile().lastModified())));
                }).collect(Collectors.toList());
        if (writeToDisk(new SiteMapIndex(siteMapList), fileName)) {
            sshUtil.upload(SiteMapConstant.SITEMAP_WEB_HOST, fileName, siteMapRemoteSavePath);
        }
    }

    private void dealIllust() {
        //取更新时间在一天内的画作
        LocalDateTime localDateTime = LocalDateTime.now().plusYears(-20);
        List<Integer> illustrationList = illustrationBizMapper.queryRecentIllustId(localDateTime);
        //判断这些画作所在的sitemap文件
        Map<Integer, List<Integer>> mapById = illustrationList.stream().collect(Collectors.groupingBy(e -> e / 50000));
        mapById.keySet().forEach(e -> {
            //重新生成sitemap
            List<Illustration> list = illustrationBizMapper.queryIllustInfoForSiteMapById(e, e + 50000);
            List<Url> urlList = list.stream().map(illustration -> new Url("https://pixivic.com/illusts/" + illustration.getId(), UTC_FORMAT.format(illustration.getCreateDate()), "never", "0.7")).collect(Collectors.toList());
            UrlSet urlSet = new UrlSet(urlList);
            //持久化illust_detail_sitemap_(index).xml
            String fileName = "illust_detail_sitemap_" + e + ".xml";
            String localFilePath = siteMapSavePath + fileName;
            if (writeToDisk(urlSet, localFilePath)) {
                //ssh对远端静态文件服务器进行移动（覆盖）sitemap文件
                if (sshUtil.upload(SiteMapConstant.SITEMAP_WEB_HOST, localFilePath, siteMapRemoteSavePath)) {
                    log.info("重生成并上传" + fileName + "成功");
                } else {
                    log.error("重生成并上传" + fileName + "失败");
                }
            }
        });

    }

    private void dealRank() {
        LocalDate localDate = LocalDate.now().plusDays(-1);
        //反序列化xml
        xStream.toXML(new UrlSet());
        String fileName = siteMapSavePath + "rank_sitemap.xml";
        UrlSet rank = (UrlSet) xStream.fromXML(new File(fileName));
        //添加元素
        for (String mode : MODES) {
            rank.getUrlList().add(new Url("https://m.pixivic.com/rank/" + mode + "/" + localDate.toString(), localDate.toString(), "never", "0.8"));
        }
        //更新sitemap文件以及对应的rank_sitemap.xml
        if (writeToDisk(rank, fileName)) {
            //ssh对远端静态文件服务器进行移动（覆盖）sitemap文件
            sshUtil.upload(SiteMapConstant.SITEMAP_WEB_HOST, fileName, siteMapRemoteSavePath);
        } else {
            log.error("排行sitemap更新失败");

        }
        log.error("排行sitemap更新成功");

    }

    private boolean writeToDisk(UrlSet urlSet, String path) {
        try (OutputStream out = new FileOutputStream(path)) {
            out.write(SiteMapConstant.DECLARATION.getBytes());
            xStream.toXML(urlSet, out);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean writeToDisk(SiteMapIndex siteMapIndex, String path) {
        try (OutputStream out = new FileOutputStream(path)) {
            out.write(SiteMapConstant.DECLARATION.getBytes());
            xStream.toXML(siteMapIndex, out);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
