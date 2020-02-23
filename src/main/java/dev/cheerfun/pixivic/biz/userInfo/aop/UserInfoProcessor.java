package dev.cheerfun.pixivic.biz.userInfo.aop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.userInfo.dto.ArtistPreViewWithFollowedInfo;
import dev.cheerfun.pixivic.biz.userInfo.dto.IllustrationWithLikeInfo;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.biz.userInfo.annotation.WithUserInfo)||@within(dev.cheerfun.pixivic.biz.userInfo.annotation.WithUserInfo)")
    public void pointCut() {
    }

    @AfterReturning(value = "pointCut()", returning = "result")
    public void withUserInfo(Object result) {
        Map<String, Object> context = AppContext.get();
        if (context != null && context.get(AuthConstant.USER_ID) != null) {
            int userId = (int) context.get(AuthConstant.USER_ID);
            if (result instanceof ResponseEntity) {
                deal(result, userId);
            } else if (result instanceof CompletableFuture) {
                ((CompletableFuture) result).thenAccept(e -> {
                    deal(e, userId);
                });
            }
        }
    }

    public void deal(Object responseEntity, Integer userId) {
        Result<List> body = (Result<List>) ((ResponseEntity) responseEntity).getBody();
        List data = body.getData();
        //由于jackson反序列化如果使用泛型则会将对象反序列化为linkedhashmap,这里重新序列化做一个转换,会降低效率
        if (data != null && data.size() > 0) {
            if (data.get(0) instanceof Map) {
                body.setData(objectMapper.convertValue(data, new TypeReference<List<Illustration>>() {
                }));
            }
            dealIsLikedInfoForIllustList(body.getData(), userId);
        }
    }

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
        int size = isLikedList.size();
        for (int i = 0; i < size; i++) {
            IllustrationWithLikeInfo illustrationWithLikeInfo = new IllustrationWithLikeInfo(illustrationList.get(i), (Boolean) isLikedList.get(i));
            illustrationWithLikeInfo.setArtistPreView(new ArtistPreViewWithFollowedInfo(illustrationWithLikeInfo.getArtistPreView(), (Boolean) isFollowedList.get(i)));
            illustrationList.set(i, illustrationWithLikeInfo);
        }
        return illustrationList;
    }

}
