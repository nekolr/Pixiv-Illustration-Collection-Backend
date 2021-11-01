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
import java.util.Arrays;
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
    private final static String[] MODES = {"day", "week", "month", "female", "male"};

    public List<Illustration> queryByDateAndMode(String date, String mode, int page, int pageSize) {
        return illustrationBizService.queryIllustrationByIdList(queryIllustIdListByDateAndMode(date, mode).stream().skip(pageSize * (page - 1))
                .limit(pageSize).collect(Collectors.toList()));
    }

    @Scheduled(cron = "0 */10 13-18 * * ?")
    public void check() {
        //检查排行是否已经爬取
        log.info("开始检查当日排行爬取情况");
        String rankDate = LocalDate.now().plusDays(-1).toString();
        cleanRankCache(rankDate);
        Boolean flag = Arrays.stream(MODES).map(e -> {
            return queryIllustIdListByDateAndMode(rankDate, e);
        }).anyMatch(e -> e == null || e.size() == 0);
        if (flag) {
            log.info("当日排行为空，开始重新爬取");
           /* while (illustIdListByDateAndMode == null || illustIdListByDateAndMode.size() == 0){

            }*/
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
        Arrays.stream(MODES).forEach(e -> {
            cacheManager.getCache("rank").evict(date + e);
        });
        /*cacheManager.getCache("rank").evict(date + "day");
        cacheManager.getCache("rank").evict(date + "week");
        cacheManager.getCache("rank").evict(date + "month");
        cacheManager.getCache("rank").evict(date + "male");
        cacheManager.getCache("rank").evict(date + "female");*/
    }

    public List<Rank> queryByDate(String date) {
        List<Rank> rankList = rankMapper.queryByDate(date);
        return rankList;
    }

}
