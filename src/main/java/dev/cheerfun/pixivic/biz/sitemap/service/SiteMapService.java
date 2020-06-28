package dev.cheerfun.pixivic.biz.sitemap.service;

import com.thoughtworks.xstream.XStream;
import dev.cheerfun.pixivic.biz.sitemap.constant.SiteMapConstant;
import dev.cheerfun.pixivic.biz.sitemap.po.SiteMapIndex;
import dev.cheerfun.pixivic.biz.sitemap.po.Url;
import dev.cheerfun.pixivic.biz.sitemap.po.UrlSet;
import dev.cheerfun.pixivic.biz.web.illust.mapper.IllustrationBizMapper;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.util.SshUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/6/28 2:20 下午
 * @description SiteMapService
 */
@Service
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

    //@Scheduled
    public void dailyTask() {
        dealIllust();
        dealRank();
        reGenerateSiteMap();

    }

    private void reGenerateSiteMap() {
        //获取illust_detail_sitemap_*文件名
        //更新sitemap文件
    }

    private void dealIllust() {
        //取更新时间在一天内的画作
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(-1);
        List<Integer> illustrationList = illustrationBizMapper.queryRecentIllustId(localDateTime);
        //判断这些画作所在的sitemap文件
        Map<Integer, List<Integer>> mapById = illustrationList.stream().collect(Collectors.groupingBy(e -> e / 5000));
        mapById.keySet().stream().parallel().forEach(e -> {
            //重新生成sitemap
            List<Illustration> list = illustrationBizMapper.queryIllustInfoForSiteMapById(e, e + 50000);
            List<Url> urlList = list.stream().map(illustration -> new Url("https://pixivic.com/illusts/" + illustration.getId(), illustration.getCreateDate().toString(), "monthly", "0.7")).collect(Collectors.toList());
            UrlSet urlSet = new UrlSet(urlList);
            //持久化illust_detail_sitemap_(index).xml
            String fileName = "illust_detail_sitemap_" + e + ".xml";
            String localFilePath = siteMapSavePath + fileName;
            String remoteFilePath = siteMapRemoteSavePath + fileName;
            writeToDisk(urlSet, localFilePath);
            //ssh对远端静态文件服务器进行移动（覆盖）sitemap文件
            try {
                if (sshUtil.upload("static", localFilePath, remoteFilePath)) {
                    log.info("重生成并上传" + fileName + "成功");
                }
            } catch (IOException ioException) {
                log.error("重生成并上传" + fileName + "失败");
                ioException.printStackTrace();
            }
        });

    }

    private void dealRank() {
        //取今日排行

        //更新sitemap文件以及对应的rank_sitemap.xml

        //ssh对远端静态文件服务器进行移动（覆盖）sitemap文件

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
