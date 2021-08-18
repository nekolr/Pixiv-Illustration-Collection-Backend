package dev.cheerfun.pixivic.biz.recommend.service;

import dev.cheerfun.pixivic.biz.recommend.domain.URRec;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NewIllustBookmarkRecommendService {
    private final RecommendSyncService recommendSyncService;
    private final StringRedisTemplate stringRedisTemplate;

    private void dealPerUser(List<Integer> u, Integer size) {
        u.stream().parallel().forEach(e -> {
            List<URRec> urRecList = recommendSyncService.queryRecommendByUser(e, size);
            //重置分数
            Set<ZSetOperations.TypedTuple<String>> typedTuples = urRecList.stream().map(recommendedItem -> {
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
            }
        });
    }
}
