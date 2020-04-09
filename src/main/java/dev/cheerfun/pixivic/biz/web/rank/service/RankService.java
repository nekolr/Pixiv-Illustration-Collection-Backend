package dev.cheerfun.pixivic.biz.web.rank.service;

import dev.cheerfun.pixivic.biz.web.rank.mapper.RankMapper;
import dev.cheerfun.pixivic.biz.web.rank.po.Rank;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class RankService {
    private final RankMapper rankMapper;

    @Cacheable(value = "rank")
    public List<Illustration> queryByDateAndMode(String date, String mode, int page, int pageSize) {
        List<Illustration> illustrationList = new ArrayList<>();
        Rank rank = rankMapper.queryByDateAndMode(date, mode);
        if (rank != null) {
            illustrationList = rank.getData().stream().skip(pageSize * (page - 1))
                    .limit(pageSize).collect(Collectors.toList());
        }
        return illustrationList;
    }

    public List<Rank> queryByDate(String date) {
        List<Rank> rankList = rankMapper.queryByDate(date);
        return rankList;
    }

}
