package dev.cheerfun.pixivic.basic.userInfo.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.common.dto.ArtistPreViewWithFollowedInfo;
import dev.cheerfun.pixivic.biz.web.common.dto.IllustrationWithLikeInfo;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/20 上午11:48
 * @description UserInfoProcessor
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Order(3)
public class UserInfoProcessor {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public List<Illustration> dealIsLikedInfoForIllustList(List<Illustration> illustrationList) {
        Map<String, Object> context = AppContext.get();
        if (context != null && context.get(AuthConstant.USER_ID) != null) {
            int userId = (int) context.get(AuthConstant.USER_ID);
            return dealIsLikedInfoForIllustList(illustrationList, userId);
        }
        return illustrationList;

    }

    public List<Illustration> dealIsLikedInfoForIllustList(List<Illustration> illustrationList, int userId) {
        List<Object> isLikedList = stringRedisTemplate.executePipelined((RedisCallback<String>) redisConnection -> {
            for (Illustration illustration : illustrationList) {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) redisConnection;
                stringRedisConnection.sIsMember(RedisKeyConstant.BOOKMARK_REDIS_PRE + userId, String.valueOf(illustration.getId()));
            }
            return null;
        });
        List<Object> isFollowedList = stringRedisTemplate.executePipelined((RedisCallback<String>) redisConnection -> {
            for (Illustration illustration : illustrationList) {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) redisConnection;
                stringRedisConnection.sIsMember(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + illustration.getArtistId(), String.valueOf(userId));
            }
            return null;
        });
     /*   Boolean isFollowed = businessService.queryIsFollowed(userId, illustration.getArtistId());
        illustration.setArtistPreView(new ArtistPreViewWithFollowedInfo(illustration.getArtistPreView(), isFollowed));*/
        int size = isLikedList.size();
        for (int i = 0; i < size; i++) {
            IllustrationWithLikeInfo illustrationWithLikeInfo = new IllustrationWithLikeInfo(illustrationList.get(i), (Boolean) isLikedList.get(i));
            illustrationWithLikeInfo.setArtistPreView(new ArtistPreViewWithFollowedInfo(illustrationWithLikeInfo.getArtistPreView(), (Boolean) isFollowedList.get(i)));
            illustrationList.set(i, illustrationWithLikeInfo);
            // illustrationList.set(i, new IllustrationWithLikeInfo(illustrationList.get(i), (Boolean) isLikedList.get(i)));
        }
        return illustrationList;
    }

    public void dealIfFollowedInfo(List<Illustration> illustrationList, int userId) {
        List<Object> isFollowedList = stringRedisTemplate.executePipelined((RedisCallback<String>) redisConnection -> {
            for (Illustration illustration : illustrationList) {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) redisConnection;
                stringRedisConnection.sIsMember(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + illustration.getArtistId(), String.valueOf(userId));
            }
            return null;
        });
        int size = isFollowedList.size();
        for (int i = 0; i < size; i++) {
            IllustrationWithLikeInfo illustrationWithLikeInfo = new IllustrationWithLikeInfo(illustrationList.get(i), true);
            illustrationWithLikeInfo.setArtistPreView(new ArtistPreViewWithFollowedInfo(illustrationWithLikeInfo.getArtistPreView(), (Boolean) isFollowedList.get(i)));
            illustrationList.set(i, illustrationWithLikeInfo);
        }
    }

}
