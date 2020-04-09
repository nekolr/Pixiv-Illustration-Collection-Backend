package dev.cheerfun.pixivic.biz.web.history.service;

import dev.cheerfun.pixivic.biz.web.history.domain.IllustHistory;
import dev.cheerfun.pixivic.biz.web.history.mapper.IllustHistoryMapper;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/12 5:24 下午
 * @description IllustLogService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IllustHistoryService {
    private final StringRedisTemplate stringRedisTemplate;
    private final IllustrationBizService illustrationBizService;
    private final IllustHistoryMapper illustHistoryMapper;

    public void push(IllustHistory illustHistory) {
        stringRedisTemplate.opsForZSet().add(RedisKeyConstant.ILLUST_BROWSING_HISTORY_REDIS_PRE + illustHistory.getUserId(), String.valueOf(illustHistory.getIllustId()), illustHistory.getCreateAt().toEpochSecond(ZoneOffset.of("+8")));
        //异步入临时表
        illustHistoryMapper.insertToTemp(illustHistory);
    }

    public List<Illustration> pullFromRedis(int userId, int page, int pageSize) {
        Set<String> illustIdList = stringRedisTemplate.opsForZSet().reverseRange(RedisKeyConstant.ILLUST_BROWSING_HISTORY_REDIS_PRE + userId, (page - 1) * pageSize, (page) * pageSize - 1);
        if (illustIdList != null && illustIdList.size() > 0) {
            return illustrationBizService.queryIllustrationByIllustIdList(illustIdList.stream().map(Integer::valueOf).collect(Collectors.toList()));
        }
        return null;
    }

    public List<Illustration> pullFromMysql(int userId, int page, int pageSize) {
        System.out.println("开始拉取早起历史记录");
        //取最小分数即时间戳
        Set<String> illustIdList = stringRedisTemplate.opsForZSet().rangeByScore(RedisKeyConstant.ILLUST_BROWSING_HISTORY_REDIS_PRE + userId, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1);
        System.out.println("画作list为" + illustIdList);
        LocalDateTime localDateTime;
        if (illustIdList != null && illustIdList.size() > 0) {
            Double timestamp = stringRedisTemplate.opsForZSet().score(RedisKeyConstant.ILLUST_BROWSING_HISTORY_REDIS_PRE + userId, illustIdList.iterator().next());
            assert timestamp != null;
            Instant instant = Instant.ofEpochMilli(timestamp.longValue());
            ZoneId zone = ZoneId.systemDefault();
            localDateTime = LocalDateTime.ofInstant(instant, zone);
        } else {
            localDateTime = LocalDateTime.now();
        }
        System.out.println("时间戳为" + localDateTime.toString());
        return illustrationBizService.queryIllustrationByIllustIdList(illustHistoryMapper.queryByUser(userId, localDateTime, (page - 1) * pageSize, pageSize));
    }

    //定时清理历史记录长度，临时表转移到正式表，清空临时表，正式表清除期限外数据
    @Scheduled(cron = "0 10 2 * * ?")
    @Transactional
    public void clear() {
        System.out.println("开始清理收藏");
        //获取keylist
        Set<String> keys = stringRedisTemplate.keys(RedisKeyConstant.ILLUST_BROWSING_HISTORY_REDIS_PRE + "*");
        //每个用户维持在1000个，超过一千从数据库取
        stringRedisTemplate.executePipelined((RedisCallback<String>) redisConnection -> {
            for (String key : keys) {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) redisConnection;
                //Long size = stringRedisConnection.zCard(key);
                //只保留五天内数据
                stringRedisConnection.zRemRangeByScore(key, LocalDateTime.now().plusDays(-5).toEpochSecond(ZoneOffset.of("+8")), -Integer.MAX_VALUE);
            }
            return null;
        });
        illustHistoryMapper.updateFromTemp();
    }
}
