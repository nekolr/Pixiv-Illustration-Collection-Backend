package dev.cheerfun.pixivic.biz.web.illust.service;

import dev.cheerfun.pixivic.biz.wallpaper.po.WallpaperCategory;
import dev.cheerfun.pixivic.biz.web.illust.secmapper.WallpaperMapper;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ImageUrl;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/23 8:23 PM
 * @description WallpaperService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager = "SecondaryTransactionManager")
public class WallpaperService {
    private final WallpaperMapper wallpaperMapper;
    private final IllustrationBizService illustrationBizService;
    private int pcWallpaperCount;
    private int mobileWallpaperCount;
    public final static String DEFAULT_IMAGE = "https://i.pixiv.cat/img-original/img/2017/12/20/00/12/19/66360679_p0.png?1605706663178";

    @PostConstruct
    public void init() {
        try {
            log.info("开始初始化壁纸服务");
            pcWallpaperCount = wallpaperMapper.queryPcWallpaperCount();
            mobileWallpaperCount = wallpaperMapper.queryMobileWallpaperCount();
        } catch (Exception e) {
            log.error("初始化壁纸服务失败");
            e.printStackTrace();
        }
        log.info("初始化壁纸服务成功");

    }

    //查询所有分类
    @Cacheable("tagCategory")
    public List<WallpaperCategory> queryAllCategory() {
        return wallpaperMapper.queryAllCate();
    }

    //分页查询分类下标签
    @Cacheable("tagListByCategory")
    public List<Tag> queryTagListByCategory(Integer categoryId, Integer offset, Integer pageSize) {
        return wallpaperMapper.queryTagListByCategory(categoryId, offset, pageSize);
    }

    //分页查询分类下标签
    @Cacheable("tagCountByCategory")
    public Integer queryTagCountByCategory(Integer categoryId) {
        return queryAllCategory().stream().filter(e -> e.getId().compareTo(categoryId) == 0).findFirst().get().getTagCount();
    }

    @Cacheable("illustIdListByTagId")
    public List<Integer> queryIllustIdByTag(Integer tagId, Integer type, Integer offset, Integer pageSize) {
        return wallpaperMapper.queryIllustIdByTag(tagId, type, offset, pageSize);
    }

    @Cacheable("illustCountByTagId")
    public Integer queryIllustCountByTag(Integer tagId, Integer type) {
        return wallpaperMapper.queryIllustCountByTag(tagId, type);
    }

    //分页查询标签下图片（图片id逆序 ）
    public List<Illustration> queryIllustByTag(Integer tagId, Integer type, Integer offset, Integer pageSize) {
        return illustrationBizService.queryIllustrationByIdList(queryIllustIdByTag(tagId, type, offset, pageSize));
    }

    public String queryRandomIllustration(int type, String size) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int illustId = queryIllustIdByRandomIndex(type, random.nextInt(1, type == 1 ? pcWallpaperCount : mobileWallpaperCount));
        List<ImageUrl> imageUrls = illustrationBizService.queryIllustrationByIdFromDb(illustId).getImageUrls();
        if (imageUrls != null && imageUrls.size() > 0) {
            ImageUrl imageUrl = imageUrls.get(0);
            switch (size) {
                case "original":
                    return imageUrl.getOriginal();
                case "large":
                    return imageUrl.getLarge();
                case "medium":
                    return imageUrl.getMedium();
                case "squareMedium":
                    return imageUrl.getSquareMedium();
            }
        }
        return DEFAULT_IMAGE;
    }

    @Cacheable("randomIllust")
    public int queryIllustIdByRandomIndex(int type, int randomIndex) {
        return type == 1 ? wallpaperMapper.queryPCIllustIdByRandomIndex(randomIndex) : wallpaperMapper.queryMobileIllustIdByRandomIndex(randomIndex);
    }

}
