package dev.cheerfun.pixivic.web.user.service;

import dev.cheerfun.pixivic.common.model.Artist;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import dev.cheerfun.pixivic.web.user.exception.BusinessException;
import dev.cheerfun.pixivic.web.user.mapper.BusinessMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private final String followRedisPre = "u:f:";

    public void bookmark(int userId, int illustId) {
        //redis中存一份联系
        stringRedisTemplate.opsForSet().add(bookmarkRedisPre + userId, String.valueOf(illustId));
        //匀速往mysql更新收藏数

    }

    public List<Illustration> queryBookmarked(int userId) {
        Set<String> illustIds = stringRedisTemplate.opsForSet().members(bookmarkRedisPre + userId);
        if (illustIds == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "收藏画作列表为空");
        }
        return businessMapper.queryBookmarked(new ArrayList<>(illustIds));
    }

    public Boolean queryIsBookmarked(int userId, String illustId) {
        return stringRedisTemplate.opsForSet().isMember(bookmarkRedisPre + userId, illustId);
    }

    public void follow(int userId, int artistId) {
        stringRedisTemplate.opsForSet().add(followRedisPre + userId, String.valueOf(artistId));
    }

    public List<Artist> queryFollowed(int userId) {
        Set<String> artists = stringRedisTemplate.opsForSet().members(followRedisPre + userId);
        if (artists == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "跟随画师列表为空");
        }
        return businessMapper.queryFollowed(new ArrayList<>(artists));
    }

    public Boolean queryIsFollowed(int userId, String artistId) {
        return stringRedisTemplate.opsForSet().isMember(followRedisPre + userId, artistId);
    }

    @Transactional
    public void addTag(String illustId, List<Tag> tags) {
        List<Tag> oldTags = businessMapper.getIllustrationTagsByid(illustId);
        oldTags.addAll(tags);
        businessMapper.updateIllustrationTagsByid(illustId, oldTags);
    }

}
