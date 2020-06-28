package dev.cheerfun.pixivic.biz.sitemap.service;

import com.thoughtworks.xstream.XStream;
import dev.cheerfun.pixivic.biz.sitemap.constant.SiteMapConstant;
import dev.cheerfun.pixivic.biz.sitemap.po.SiteMapIndex;
import dev.cheerfun.pixivic.biz.sitemap.po.UrlSet;
import dev.cheerfun.pixivic.common.util.SshUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/6/28 2:20 下午
 * @description SiteMapService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SiteMapService {
    private final SshUtil sshUtil;
    private final XStream xStream;

    //@Scheduled
    public void dailyTask() {
        dealIllust();
        dealRank();

    }

    private void dealIllust() {
        //取更新时间在一天内的画作
        //判断这些画作所在的sitemap文件
        //更新sitemap文件以及对应的illust_detail_sitemap_(index).xml
        //ssh对远端静态文件服务器进行移动（覆盖）sitemap文件

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
