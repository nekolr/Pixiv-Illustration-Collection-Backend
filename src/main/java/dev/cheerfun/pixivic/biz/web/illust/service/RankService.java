package dev.cheerfun.pixivic.biz.web.illust.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.service.IllustRankService;
import dev.cheerfun.pixivic.biz.web.illust.secmapper.RankMapper;
import dev.cheerfun.pixivic.biz.web.illust.po.Rank;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager = "SecondaryTransactionManager")
public class RankService {
    private final RankMapper rankMapper;
    private final ObjectMapper objectMapper;
    private final IllustrationBizService illustrationBizService;
    private final CacheManager cacheManager;
    private final IllustRankService illustRankService;

    public List<Illustration> queryByDateAndMode(String date, String mode, int page, int pageSize) {
        return illustrationBizService.queryIllustrationByIdList(queryIllustIdListByDateAndMode(date, mode).stream().skip(pageSize * (page - 1))
                .limit(pageSize).collect(Collectors.toList()));
    }

    @Scheduled(cron = "0 10 12,13,14 * * ?")
    public void check() {
        //检查排行是否已经爬取
        log.info("开始检查当日排行爬取情况");
        String rankDate = LocalDate.now().plusDays(-1).toString();
        cleanRankCache(rankDate);
        final List<Integer> illustIdListByDateAndMode = queryIllustIdListByDateAndMode(rankDate, "day");
        if (illustIdListByDateAndMode == null || illustIdListByDateAndMode.size() == 1) {
            log.info("当日排行为空，开始重新爬取");
            illustRankService.pullAllRank(rankDate);
            cleanRankCache(rankDate);
        } else {
            log.info("当日排行已经爬取");
        }
    }

    @Cacheable(value = "rank", key = "#date+#mode")
    public List<Integer> queryIllustIdListByDateAndMode(String date, String mode) {
        List<Integer> illustrationIdList = new ArrayList<>();
        Rank rank = rankMapper.queryByDateAndMode(date, mode);
        if (rank != null) {
            illustrationIdList = objectMapper.convertValue(rank.getData(), new TypeReference<List<Illustration>>() {
            }).stream().map(Illustration::getId).collect(Collectors.toList());
        }
        return illustrationIdList;
    }

    public void cleanRankCache(String date) {
        cacheManager.getCache("rank").evict(date + "day");
        cacheManager.getCache("rank").evict(date + "week");
        cacheManager.getCache("rank").evict(date + "month");
        cacheManager.getCache("rank").evict(date + "male");
        cacheManager.getCache("rank").evict(date + "female");
    }

    public List<Rank> queryByDate(String date) {
        List<Rank> rankList = rankMapper.queryByDate(date);
        return rankList;
    }

}
