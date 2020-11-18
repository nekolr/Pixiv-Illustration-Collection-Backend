package dev.cheerfun.pixivic.biz.wallpaper.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.wallpaper.constant.WallpaperType;
import dev.cheerfun.pixivic.biz.wallpaper.secmapper.WallpaperGenerateMapper;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/21 8:27 PM
 * @description WallpaperGenerateService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WallpaperGenerateService {
    private final IllustrationBizService illustrationBizService;
    private final WallpaperGenerateMapper wallpaperGenerateMapper;
    private final ObjectMapper objectMapper;
    private final float computer = 16f / 9;
    private final float phone = 9f / 16;

    /* *
     */
//@PostConstruct
    public void generateWallpaper() {
        //从数据库拉取画作数据（时间维度限制，情色级别小于等于6，收藏数收藏数大于200）
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.plusYears(-3);
        List<Integer> illustIdList = queryIllustIdFilterForWallpaper(start, end);
        //进行持久化
        saveToDb(illustIdList);
    }

    private List<Integer> queryIllustIdFilterForWallpaper(LocalDateTime start, LocalDateTime end) {
        return wallpaperGenerateMapper.queryIllustIdFilterForWallpaper(start, end);
    }

    private Integer filterBySize(Illustration illustration) {
        float rate = ((float) illustration.getWidth()) / illustration.getHeight();
        //电脑比例
        if (rate >= computer - 0.2 && rate <= computer + 0.2) {
            if (illustration.getHeight() >= 1080 && illustration.getWidth() >= 1920) {
                return WallpaperType.COMPUTER;
            }
        }
        //手机比例
        if (rate >= phone - 0.2 && rate <= phone + 0.2) {
            if (illustration.getWidth() >= 1080 && illustration.getHeight() >= 1920) {
                return WallpaperType.PHONE;
            }
        }
        return null;
    }

    private void saveToDb(List<Integer> illustIdList) {
        illustIdList.stream().parallel().filter(Objects::nonNull).forEach(e -> {
            try {
                Illustration illustration = illustrationBizService.queryIllustrationByIdFromDbWithoutCache(e);
                //在内存中进行筛选（长宽比例限制，长宽限制）电脑 16:9 左右比例 长宽都要大于1080*1920 手机9:16左右比例 长宽同样要有要求
                Integer type = filterBySize(illustration);
                if (type != null) {
                    //过滤没有中文释义的标签
                    illustration.getTags().stream().filter(t -> {
                        String translatedName = t.getTranslatedName();
                        return t.getId() != null && translatedName != null && !"".equals(translatedName.trim());
                    }).forEach(t -> {
                        //构造插入wallpapers表
                        wallpaperGenerateMapper.insertWallpaper(t.getId(), type, illustration.getId());
                    });
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });
    }
}
