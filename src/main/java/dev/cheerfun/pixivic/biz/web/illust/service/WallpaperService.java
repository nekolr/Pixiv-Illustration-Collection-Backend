package dev.cheerfun.pixivic.biz.web.illust.service;

import dev.cheerfun.pixivic.biz.wallpaper.po.WallpaperCategory;
import dev.cheerfun.pixivic.biz.web.illust.secmapper.WallpaperMapper;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/23 8:23 PM
 * @description WallpaperService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class WallpaperService {
    private final WallpaperMapper wallpaperMapper;
    private final IllustrationBizService illustrationBizService;

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

}
