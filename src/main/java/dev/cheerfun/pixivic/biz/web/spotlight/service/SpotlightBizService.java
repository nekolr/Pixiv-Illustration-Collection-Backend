package dev.cheerfun.pixivic.biz.web.spotlight.service;

import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.biz.web.spotlight.secmapper.SpotlightBizMapper;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Spotlight;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/12 17:01
 * @description RankService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SpotlightBizService {
    private final IllustrationBizService illustrationBizService;
    private final SpotlightBizMapper spotlightBizMapper;

    public List<Spotlight> query(int page, int pageSize) {
        return spotlightBizMapper.queryList(pageSize, (page - 1) * pageSize).stream().map(this::queryDetail).collect(Collectors.toList());
    }

    @Cacheable(value = "spotlight")
    public Spotlight queryDetail(int spotlightId) {
        return spotlightBizMapper.queryDetail(spotlightId);
    }

    public List<Illustration> queryIllustrations(int spotlightId) {
        return illustrationBizService.queryIllustrationByIdList(spotlightBizMapper.queryIllustrations(spotlightId));
    }
}
