package dev.cheerfun.pixivic.biz.web.user.service;

import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.user.mapper.BusinessMapper;
import dev.cheerfun.pixivic.common.model.Artist;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final String followRedisPre = "u:f:";

    public void bookmark(int userId, int illustId) {
        bookmarkOperation(userId, illustId, 1);
    }

    public void cancelBookmark(int userId, int illustId) {
        bookmarkOperation(userId, illustId, -1);
    }

    private void bookmarkOperation(int userId, int illustId, int increment) {
        //redis修改联系以及修改redis中该画作收藏数(事务)
        if ((increment > 0 && stringRedisTemplate.opsForSet().isMember(bookmarkRedisPre + userId, String.valueOf(illustId)))
                || (increment < 0 && !stringRedisTemplate.opsForSet().isMember(bookmarkRedisPre + userId, String.valueOf(illustId)))
        ) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户与画作的收藏关系请求错误");
        }
        stringRedisTemplate.execute(new SessionCallback<>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                if (increment > 0) {
                    operations.opsForSet().add(bookmarkRedisPre + userId, String.valueOf(illustId));
                } else {
                    operations.opsForSet().remove(bookmarkRedisPre + userId, String.valueOf(illustId));
                }
                operations.opsForHash().increment(bookmarkCountMapRedisPre, String.valueOf(illustId), increment);
                return operations.exec();
            }
        });
    }

    //@Scheduled(cron = "0 0 16 * * ?")
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

    public List<Illustration> queryBookmarked(int userId, int currIndex, int pageSize) {
        List<Integer> illustIds = stringRedisTemplate.opsForSet().members(bookmarkRedisPre + userId).stream().map(Integer::parseInt).collect(Collectors.toList());
        if (illustIds.size() == 0) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "收藏画作列表为空");
        }
        return businessMapper.queryBookmarked(illustIds, currIndex, pageSize);
    }

    public Boolean queryIsBookmarked(int userId, String illustId) {
        return stringRedisTemplate.opsForSet().isMember(bookmarkRedisPre + userId, illustId);
    }

    public void follow(int userId, int artistId) {
        stringRedisTemplate.opsForSet().add(followRedisPre + userId, String.valueOf(artistId));
    }

    public void cancelFollow(int userId, int artistId) {
        stringRedisTemplate.opsForSet().remove(followRedisPre + userId, String.valueOf(artistId));
    }

    public List<Artist> queryFollowed(int userId, int currIndex, int pageSize) {
        List<Integer> artists = stringRedisTemplate.opsForSet().members(followRedisPre + userId).stream().map(Integer::parseInt).collect(Collectors.toList());
        if (artists.size() == 0) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "跟随画师列表为空");
        }
        return businessMapper.queryFollowed(artists, currIndex, pageSize);
    }

    public Boolean queryIsFollowed(int userId, String artistId) {
        return stringRedisTemplate.opsForSet().isMember(followRedisPre + userId, artistId);
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

}
