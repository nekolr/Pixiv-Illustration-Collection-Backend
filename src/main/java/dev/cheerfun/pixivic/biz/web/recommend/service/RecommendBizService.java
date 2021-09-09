package dev.cheerfun.pixivic.biz.web.recommend.service;

import dev.cheerfun.pixivic.biz.userInfo.dto.ArtistWithIsFollowedInfo;
import dev.cheerfun.pixivic.biz.web.artist.service.ArtistBizService;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.biz.web.user.dto.ArtistWithRecentlyIllusts;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/8 2:09 下午
 * @description RecommendBizService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecommendBizService {
    private final IllustrationBizService illustrationBizService;
    private final ArtistBizService artistBizService;
    private final StringRedisTemplate stringRedisTemplate;
    private final Random random = new Random(21);

    public List<Illustration> queryRecommendBookmarkIllust(Integer userId, Integer page, Integer pageSize) {
        Set<ZSetOperations.TypedTuple<String>> illustIdList = stringRedisTemplate.opsForZSet().reverseRangeWithScores(RedisKeyConstant.USER_RECOMMEND_BOOKMARK_ILLUST + userId, pageSize * (page - 1), page * pageSize);
        if (illustIdList != null && illustIdList.size() > 0) {
            //异步降级
            List<Illustration> illustrationList = illustrationBizService.queryIllustrationByIdList(illustIdList.stream().map(e -> Integer.parseInt(e.getValue())).collect(Collectors.toList()));
            downGrade(RedisKeyConstant.USER_RECOMMEND_BOOKMARK_ILLUST + userId, illustIdList);
            return illustrationList;
        }
        return null;
    }

    public List<Illustration> queryRecommendViewIllust(Integer userId, Integer page, Integer pageSize) {
        Set<ZSetOperations.TypedTuple<String>> illustIdList = stringRedisTemplate.opsForZSet().reverseRangeWithScores(RedisKeyConstant.USER_RECOMMEND_VIEW_ILLUST + userId, pageSize * (page - 1), page * pageSize);
        if (illustIdList != null && illustIdList.size() > 0) {
            //异步降级
            List<Illustration> illustrationList = illustrationBizService.queryIllustrationByIdList(illustIdList.stream().map(e -> Integer.parseInt(e.getValue())).collect(Collectors.toList()));
            downGrade(RedisKeyConstant.USER_RECOMMEND_VIEW_ILLUST + userId, illustIdList);
            return illustrationList;
        }
        return null;
    }

    public List<Artist> queryRecommendArtist(Integer userId, Integer page, Integer pageSize) {
        Set<ZSetOperations.TypedTuple<String>> artistIdList = stringRedisTemplate.opsForZSet().reverseRangeWithScores(RedisKeyConstant.USER_RECOMMEND_ARTIST + userId, pageSize * (page - 1), page * pageSize);
        if (artistIdList != null && artistIdList.size() > 0) {
            List<Artist> artistList = artistIdList.stream().map(e -> {
                        Artist artist = artistBizService.queryArtistById(Integer.parseInt(e.getValue()));
                        artistBizService.dealArtist(artist);
                        return new ArtistWithIsFollowedInfo(artist, stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + e.getValue(), String.valueOf(userId)));
                    }
            ).collect(Collectors.toList());
            artistList = artistList.stream().map(artist -> {
                List<Illustration> illustrationList = null;
                try {
                    illustrationList = artistBizService.queryIllustrationsByArtistId(artist.getId(), "illust", 0, 30).stream().limit(3).collect(Collectors.toList());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return new ArtistWithRecentlyIllusts(artist, illustrationList);
            }).collect(Collectors.toList());
            //异步降级
            downGrade(RedisKeyConstant.USER_RECOMMEND_ARTIST + userId, artistIdList);
            return artistList;
        }
        return null;
    }

    @Async("recommendExecutorService")
    public void downGrade(String key, Set<ZSetOperations.TypedTuple<String>> set) {
        set.forEach(e -> {
            try {
                double result = e.getScore() - random.nextInt(21);
                if (result < 0) {
                    stringRedisTemplate.opsForZSet().remove(key, e.getValue());
                } else {
                    stringRedisTemplate.opsForZSet().add(key, e.getValue(), result);
                }
            } catch (Exception exception) {
                log.error("随机降级出错");
                exception.printStackTrace();
            }
        });
    }

    public void deleteFromRecommendationSet(Integer userId, String key, Integer targetId) {
        stringRedisTemplate.opsForZSet().remove(key + userId, String.valueOf(targetId));
    }

}
