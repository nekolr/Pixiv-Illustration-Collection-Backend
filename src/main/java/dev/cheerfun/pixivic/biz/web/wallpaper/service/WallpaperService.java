package dev.cheerfun.pixivic.biz.web.wallpaper.service;

import dev.cheerfun.pixivic.biz.wallpaper.po.WallpaperCategory;
import dev.cheerfun.pixivic.biz.web.wallpaper.mapper.WallpaperMapper;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/23 8:23 PM
 * @description WallpaperService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WallpaperService {
    private final WallpaperMapper wallpaperMapper;

    //查询所有分类
    @Cacheable("tagCategory")
    public List<WallpaperCategory> queryAllCate() {
        return wallpaperMapper.queryAllCate();
    }

    //分页查询分类下标签
    @Cacheable("tagListByCategory")
    public List<Tag> queryTagListByCategory(Integer categoryId, Integer offset, Integer pageSize) {
        return wallpaperMapper.queryTagListByCategory(categoryId, offset, pageSize);
    }

    //分页查询标签下图片（图片id逆序 ）
    @Cacheable("illustIdListByTagId")
    public List<Integer> queryIllustIdByTag(Integer tagId, Integer type, Integer offset, Integer pageSize) {
        return wallpaperMapper.queryIllustIdByTag(tagId, type, offset, pageSize);
    }

}
