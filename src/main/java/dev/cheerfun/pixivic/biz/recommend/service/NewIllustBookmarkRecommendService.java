package dev.cheerfun.pixivic.biz.recommend.service;

import dev.cheerfun.pixivic.biz.recommend.domain.URRec;
import dev.cheerfun.pixivic.biz.recommend.mapper.RecommendMapper;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/8/18 11:09 下午
 * @description NewIllustBookmarkRecommendService
 */

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NewIllustBookmarkRecommendService {
    private final RecommendSyncService recommendSyncService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RecommendMapper recommendMapper;
    private LocalDateTime localDateTimeIndex = LocalDateTime.now();

    public void recommend() throws TasteException {
        log.info("开始生成推荐，当前内存消耗" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + " M");
        //根据活跃度分级生成
        LocalDate now = LocalDate.now();
        String today = now.plusDays(2).toString();
        String threeDaysAgo = now.plusDays(-3).toString();
        String sixDaysAgo = now.plusDays(-6).toString();
        String twelveDaysAgo = now.plusDays(-12).toString();
        String monthAgo = now.plusDays(-30).toString();

        //清理推荐
        //不活跃用户推荐删除
        recommendMapper.queryUserIdByDateBefore(monthAgo).forEach(e -> {
            stringRedisTemplate.delete(RedisKeyConstant.USER_RECOMMEND_BOOKMARK_ILLUST + e);
        });

        List<Integer> u1 = recommendMapper.queryUserIdByDateRange(threeDaysAgo, today);
        dealPerUser(u1, 3000);
        List<Integer> u2 = recommendMapper.queryUserIdByDateRange(sixDaysAgo, threeDaysAgo);
        dealPerUser(u2, 2000);
        List<Integer> u3 = recommendMapper.queryUserIdByDateRange(twelveDaysAgo, sixDaysAgo);
        dealPerUser(u3, 1000);
        System.gc();
        log.info("垃圾清理结束，当前内存消耗" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + " M");

    }

    public void recommendForNewUser() {
        LocalDateTime now = LocalDateTime.now();
        List<Integer> userList = recommendMapper.queryUserIdByCreateDateRange(localDateTimeIndex, now);
        localDateTimeIndex = now;
        dealPerUser(userList, 600);
    }

    public void dealPerUser(List<Integer> userList, Integer size) {
        userList.stream().parallel().forEach(e -> {
            try {
                List<URRec> urRecList = recommendSyncService.queryRecommendIllustByUser(e, size);
                //重置分数
                Set<ZSetOperations.TypedTuple<String>> typedTuples = urRecList.stream()
                        //过滤已经收藏的
                        .filter(r -> Boolean.FALSE.equals(stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.BOOKMARK_REDIS_PRE + e, r.getItem())))
                        .map(recommendedItem -> {
                                    Double score = stringRedisTemplate.opsForZSet().score(RedisKeyConstant.USER_RECOMMEND_BOOKMARK_ILLUST + e, String.valueOf(recommendedItem.getItem()));
                                    if (score == null) {
                                        score = (double) recommendedItem.getScore();
                                    }
                                    return new DefaultTypedTuple<>(String.valueOf(recommendedItem.getItem()), score);
                                }
                        ).collect(Collectors.toSet());
                if (typedTuples.size() > 0) {
                    //清空
                    stringRedisTemplate.delete(RedisKeyConstant.USER_RECOMMEND_BOOKMARK_ILLUST + e);
                    //新增
                    stringRedisTemplate.opsForZSet().add(RedisKeyConstant.USER_RECOMMEND_BOOKMARK_ILLUST + e, typedTuples);
                    urRecList = null;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });
    }
}
