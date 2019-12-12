package dev.cheerfun.pixivic.biz.web.news.service;

import dev.cheerfun.pixivic.biz.web.news.mapper.NewsBIZMapper;
import dev.cheerfun.pixivic.common.po.ACGNew;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/10/20 上午11:41
 * @description NewsBIZService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NewsBIZService {
    private final NewsBIZMapper newsBIZMapper;
    @Cacheable(value = "new")
    public List<ACGNew> queryByFromAndDate(String referer, String date, int page, int pageSize) {
        System.out.println("未命中缓存");
        return newsBIZMapper.queryByFromAndDate(referer,date,pageSize*(page-1),pageSize);
    }
}
