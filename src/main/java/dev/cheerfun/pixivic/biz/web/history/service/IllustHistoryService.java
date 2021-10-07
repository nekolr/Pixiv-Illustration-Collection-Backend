package dev.cheerfun.pixivic.biz.web.history.service;

import dev.cheerfun.pixivic.biz.web.history.domain.IllustHistory;
import dev.cheerfun.pixivic.biz.web.history.mapper.IllustHistoryMapper;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/12 5:24 下午
 * @description IllustLogService
 */
@Service
@Slf4j
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
        return illustrationBizService.queryIllustrationByIllustIdList(pullIdFromMysql(userId, page, pageSize));
    }

    @Cacheable("illustHistoryFromMysql")
    public List<Integer> pullIdFromMysql(int userId, int page, int pageSize) {
        //取最小分数即时间戳
        Set<String> illustIdList = stringRedisTemplate.opsForZSet().rangeByScore(RedisKeyConstant.ILLUST_BROWSING_HISTORY_REDIS_PRE + userId, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1);
        LocalDateTime localDateTime;
        if (illustIdList != null && illustIdList.size() > 0) {
            Double timestamp = stringRedisTemplate.opsForZSet().score(RedisKeyConstant.ILLUST_BROWSING_HISTORY_REDIS_PRE + userId, illustIdList.iterator().next());
            assert timestamp != null;
            Instant instant = Instant.ofEpochSecond(timestamp.longValue());
            ZoneId zone = ZoneId.systemDefault();
            localDateTime = LocalDateTime.ofInstant(instant, zone);
        } else {
            localDateTime = LocalDateTime.now();
        }
        return illustHistoryMapper.queryByUser(userId, localDateTime, (page - 1) * pageSize, pageSize);
    }

    //定时清理历史记录长度，临时表转移到正式表，清空临时表，正式表清除期限外数据
    @Scheduled(cron = "0 10 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict("illustHistoryFromMysql")
    public void clear() {
        log.info("开始清理历史记录");
        Iterator<RedisClusterNode> iterator = stringRedisTemplate.getConnectionFactory().getClusterConnection().clusterGetNodes().iterator();
        while (iterator.hasNext()) {
            RedisClusterNode clusterNode = iterator.next();
            Set<String> keys = stringRedisTemplate.opsForCluster().keys(clusterNode, RedisKeyConstant.ILLUST_BROWSING_HISTORY_REDIS_PRE + "*");
            stringRedisTemplate.unlink(keys);
        }
     /*   //获取keylist
        final ScanOptions scanOptions = ScanOptions.scanOptions().match(RedisKeyConstant.ILLUST_BROWSING_HISTORY_REDIS_PRE + "*").build();
        RedisConnection connection = stringRedisTemplate.getConnectionFactory().getConnection();
        Cursor<byte[]> cursor = connection.scan(scanOptions);
        int i = 0;
        while (cursor.hasNext()) {
            connection.zRemRangeByScore(cursor.next(), LocalDateTime.now().plusDays(-3).toEpochSecond(ZoneOffset.of("+8")), -Integer.MAX_VALUE);
            i++;
        }*/
        illustHistoryMapper.deleteIllustHistory();
        illustHistoryMapper.tempToIllustHistory();
        illustHistoryMapper.truncateTemp();
    }

    @Transactional
    public Boolean deleteByUserId(Integer userId) {
        Iterator<RedisClusterNode> iterator = stringRedisTemplate.getConnectionFactory().getClusterConnection().clusterGetNodes().iterator();
        while (iterator.hasNext()) {
            RedisClusterNode clusterNode = iterator.next();
            Set<String> keys = stringRedisTemplate.opsForCluster().keys(clusterNode, RedisKeyConstant.ILLUST_BROWSING_HISTORY_REDIS_PRE + userId);
            stringRedisTemplate.unlink(keys);
        }
        illustHistoryMapper.deleteIllustHistorByUserId(userId);
        return true;
    }

}
