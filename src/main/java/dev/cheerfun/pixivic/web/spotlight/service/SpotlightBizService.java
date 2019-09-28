package dev.cheerfun.pixivic.web.spotlight.service;

import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.Spotlight;
import dev.cheerfun.pixivic.web.spotlight.mapper.SpotlightBizMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/12 17:01
 * @description RankService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SpotlightBizService {
    private final SpotlightBizMapper spotlightBizMapper;

    public List<Spotlight> query(int page, int pageSize) {
       return spotlightBizMapper.queryList(pageSize,(page-1)*pageSize);
    }

    public Spotlight queryDetail(int spotlightId) {
        return spotlightBizMapper.queryDetail(spotlightId);
    }

    public List<Illustration> queryIllustrations(int spotlightId) {
        return spotlightBizMapper.queryIllustrations(spotlightId);
    }
}
