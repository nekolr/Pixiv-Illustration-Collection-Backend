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

    @Cacheable(value = "newList")
    public List<ACGNew> queryByFromAndDate(String referer, int page, int pageSize) {
        return newsBIZMapper.queryByFromAndDate(referer, pageSize * (page - 1), pageSize);
    }

    @Cacheable(value = "new")
    public ACGNew queryNewById(Integer newId) {
        return newsBIZMapper.queryNewById(newId);
    }
}
