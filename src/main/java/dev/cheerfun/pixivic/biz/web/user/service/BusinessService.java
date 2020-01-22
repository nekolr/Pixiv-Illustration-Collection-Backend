package dev.cheerfun.pixivic.biz.web.user.service;

import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.dto.IllustrationWithLikeInfo;
import dev.cheerfun.pixivic.biz.web.user.mapper.BusinessMapper;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/19 20:51
 * @description BusinessService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BusinessService {
    private final StringRedisTemplate stringRedisTemplate;
    private final BusinessMapper businessMapper;
    private final String bookmarkRedisPre = "u:b:";
    private final String bookmarkCountMapRedisPre = "u:bcm";
    private final String userFollowRedisPre = "u:f:";
    private final String artistFollowRedisPre = "a:f:";
    private static final String USER_ID = "userId";

    public void bookmark(int userId, int illustId) {
        bookmarkOperation(userId, illustId, 1, 0);
    }

    public void cancelBookmark(int userId, int illustId, int relationId) {
        bookmarkOperation(userId, illustId, -1, relationId);
    }

    void bookmarkOperation(int userId, int illustId, int increment, int relationId) {
        //redis修改联系以及修改redis中该画作收藏数(事务)
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(bookmarkRedisPre + userId, String.valueOf(illustId));
        if ((increment > 0 && isMember)
                || (increment < 0 && !isMember)
        ) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户与画作的收藏关系请求错误");
        }
        stringRedisTemplate.execute(new SessionCallback<>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                if (increment > 0) {
                    operations.opsForSet().add(bookmarkRedisPre + userId, String.valueOf(illustId));
                    //异步往mysql中写入
                    businessMapper.bookmark(userId, illustId, LocalDateTime.now());
                } else {
                    operations.opsForSet().remove(bookmarkRedisPre + userId, String.valueOf(illustId));
                    //异步往mysql中移除
                    businessMapper.cancelBookmark(userId, illustId);
                }
                operations.opsForHash().increment(bookmarkCountMapRedisPre, String.valueOf(illustId), increment);
                return operations.exec();
            }
        });
    }

    public List<Illustration> dealIsLikedInfoForIllustList(List<Illustration> illustrationList) {
        if (AppContext.get() != null) {
            int userId = (int) AppContext.get().get(USER_ID);
            return dealIsLikedInfoForIllustList(illustrationList, userId);
        }
        return illustrationList;

    }

    public List<Illustration> dealIsLikedInfoForIllustList(List<Illustration> illustrationList, int userId) {
        List<Object> isLikedList = stringRedisTemplate.executePipelined((RedisCallback<String>) redisConnection -> {
            for (Illustration illustration : illustrationList) {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) redisConnection;
                stringRedisConnection.sIsMember(bookmarkRedisPre + userId, String.valueOf(illustration.getId()));
            }
            return null;
        });
        List<Illustration> illustrationWithLikeInfoList = new ArrayList<>();
        int size = isLikedList.size();
        for (int i = 0; i < size; i++) {
            IllustrationWithLikeInfo illustrationWithLikeInfo = new IllustrationWithLikeInfo(illustrationList.get(i), (Boolean) isLikedList.get(i));
            illustrationWithLikeInfoList.add(illustrationWithLikeInfo);
        }
        return illustrationWithLikeInfoList;
    }

    //@Scheduled(cron = "0 0 16 * * ?")
    @Transactional
    public void flushBookmarkCountToDb() {
        //半夜三点往mysql更新收藏数
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(bookmarkCountMapRedisPre);
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            int illustId = Integer.parseInt(entry.getKey().toString());
            int increment = Integer.parseInt(entry.getValue().toString());
            businessMapper.updateIllustBookmark(illustId, increment);
        }
        stringRedisTemplate.delete(bookmarkCountMapRedisPre);
    }

    public List<Illustration> queryBookmarked(int userId, String type, int currIndex, int pageSize) {
        return businessMapper.queryBookmarked(userId, type, currIndex, pageSize);
    }

    public Boolean queryIsBookmarked(int userId, Integer illustId) {
        return stringRedisTemplate.opsForSet().isMember(bookmarkRedisPre + userId, String.valueOf(illustId));
    }

    public void follow(int userId, int artistId) {
        businessMapper.follow(userId, artistId, LocalDateTime.now());
    }

    public void cancelFollow(int userId, int artistId) {
        int effectRow = businessMapper.cancelFollow(userId, artistId);
        if (effectRow == 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户画师关系请求错误");
        }
    }

    public List<Artist> queryFollowed(int userId, int currIndex, int pageSize) {
        List<Artist> artists = businessMapper.queryFollowed(userId, currIndex, pageSize);
        if (artists.size() == 0) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "跟随画师列表为空");
        }
        return artists;
    }

    public Boolean queryIsFollowed(int userId, Integer artistId) {
        return businessMapper.queryIsFollowed(userId, artistId) != 0;
    }

    @Transactional
    public void addTag(int userId, String illustId, List<Tag> tags) {
        List<Tag> oldTags = businessMapper.queryIllustrationTagsById(illustId);
        oldTags.addAll(tags);
        businessMapper.updateIllustrationTagsById(illustId, oldTags);
        //用户积分增加
        int starIncrement = 10;
        businessMapper.updateUserStar(userId, starIncrement);
    }

    public List<Illustration> queryFollowedLatest(int userId, String type, int currIndex, int pageSize) {
        List<Illustration> illustrations = businessMapper.queryFollowedLatest(userId, type, currIndex, pageSize);
        if (illustrations.size() == 0) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "跟随画师列表为空");
        }
        return illustrations;
    }
}
