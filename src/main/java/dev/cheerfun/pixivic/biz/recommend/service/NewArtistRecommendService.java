package dev.cheerfun.pixivic.biz.recommend.service;

import dev.cheerfun.pixivic.biz.recommend.domain.URRec;
import dev.cheerfun.pixivic.biz.recommend.mapper.RecommendMapper;
import dev.cheerfun.pixivic.biz.web.artist.service.ArtistBizService;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.po.ArtistSummary;
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
 * @date 2021/8/27 11:11 下午
 * @description NewArtistRecommendService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NewArtistRecommendService {

    private final RecommendSyncService recommendSyncService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RecommendMapper recommendMapper;
    private final ArtistBizService artistBizService;
    private LocalDateTime localDateTimeIndex = LocalDateTime.now();

    public void recommend() throws TasteException {
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
            stringRedisTemplate.delete(RedisKeyConstant.USER_RECOMMEND_ARTIST + e);
        });

        List<Integer> u1 = recommendMapper.queryUserIdByDateRange(threeDaysAgo, today);
        dealPerUser(u1, 400, false);
        System.gc();
        List<Integer> u2 = recommendMapper.queryUserIdByDateRange(sixDaysAgo, threeDaysAgo);
        dealPerUser(u2, 300, false);
        System.gc();
        List<Integer> u3 = recommendMapper.queryUserIdByDateRange(twelveDaysAgo, sixDaysAgo);
        dealPerUser(u3, 100, false);
        System.gc();

    }

    public void dealPerUser(List<Integer> userList, Integer size, Boolean isNewUser) {
        userList.stream().parallel().forEach(e -> {
            try {
                List<URRec> urRecList = null;
                Set<ZSetOperations.TypedTuple<String>> typedTuples = null;
                //重置分数
                if (isNewUser) {
                    urRecList = recommendSyncService.queryRecommendArtistForNewUser();
                    typedTuples = urRecList.stream().map(u -> new DefaultTypedTuple<>(String.valueOf(u.getItem()), (double) u.getScore())).collect(Collectors.toSet());
                } else {
                    urRecList = recommendSyncService.queryRecommendArtistByUser(e, size);
                    typedTuples = urRecList.stream()
                            //过滤已经收藏的
                            .filter(r -> Boolean.FALSE.equals(stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + r.getItem(), String.valueOf(e))))
                            .filter(r -> {
                                ArtistSummary artistSummary = artistBizService.querySummaryByArtistId(Integer.valueOf(r.getItem()));
                                return artistSummary != null && artistSummary.getIllustSum() != null && artistSummary.getIllustSum() != 0;
                            })
                            .map(recommendedItem -> {
                                        Double score = stringRedisTemplate.opsForZSet().score(RedisKeyConstant.USER_RECOMMEND_ARTIST + e, String.valueOf(recommendedItem.getItem()));
                                        if (score == null) {
                                            score = (double) recommendedItem.getScore();
                                        }
                                        return new DefaultTypedTuple<>(String.valueOf(recommendedItem.getItem()), score);
                                    }
                            ).collect(Collectors.toSet());
                }

                if (typedTuples.size() > 0) {
                    //清空
                    stringRedisTemplate.delete(RedisKeyConstant.USER_RECOMMEND_ARTIST + e);
                    //新增
                    stringRedisTemplate.opsForZSet().add(RedisKeyConstant.USER_RECOMMEND_ARTIST + e, typedTuples);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });
    }

    public void recommendForNewUser() {
        LocalDateTime now = LocalDateTime.now();
        List<Integer> userList = recommendMapper.queryUserIdByCreateDateRange(localDateTimeIndex, now);
        localDateTimeIndex = now;
        dealPerUser(userList, 200, true);
    }

}
