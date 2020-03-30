package dev.cheerfun.pixivic.biz.web.user.service;

import com.google.common.collect.Lists;
import dev.cheerfun.pixivic.biz.userInfo.dto.ArtistPreViewWithFollowedInfo;
import dev.cheerfun.pixivic.biz.userInfo.dto.ArtistWithIsFollowedInfo;
import dev.cheerfun.pixivic.biz.userInfo.dto.IllustrationWithLikeInfo;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.biz.web.user.dto.ArtistWithRecentlyIllusts;
import dev.cheerfun.pixivic.biz.web.user.mapper.BusinessMapper;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
import java.util.PriorityQueue;
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
    private final IllustrationBizService illustrationBizService;

    public void bookmark(int userId, String username, int illustId) {
        bookmarkOperation(userId, username, illustId, 1, 0);
    }

    public void cancelBookmark(int userId, int illustId, int relationId) {
        bookmarkOperation(userId, null, illustId, -1, relationId);
    }

    @Transactional
    @CacheEvict(value = "illust_bookmarked", key = "#illustId+'1'+'30'")
    public void bookmarkOperation(int userId, String username, int illustId, int increment, int relationId) {
        //redis修改联系以及修改redis中该画作收藏数(事务)
//        Boolean isMember = stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.BOOKMARK_REDIS_PRE + userId, String.valueOf(illustId));
//        if ((increment > 0 && isMember)
//                || (increment < 0 && !isMember)
//        ) {
//            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户与画作的收藏关系请求错误");
//        }
        stringRedisTemplate.execute(new SessionCallback<>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                if (increment > 0) {
                    operations.opsForSet().add(RedisKeyConstant.BOOKMARK_REDIS_PRE + userId, String.valueOf(illustId));
                    //异步往mysql中写入
                    businessMapper.bookmark(userId, illustId, username, LocalDateTime.now());
                } else {
                    operations.opsForSet().remove(RedisKeyConstant.BOOKMARK_REDIS_PRE + userId, String.valueOf(illustId));
                    //异步往mysql中移除
                    businessMapper.cancelBookmark(userId, illustId);
                }
                operations.opsForHash().increment(RedisKeyConstant.BOOKMARK_COUNT_MAP_REDIS_PRE, String.valueOf(illustId), increment);
                return operations.exec();
            }
        });
    }

    public void dealIsLikedInfoForIllustList(List<Illustration> illustrationList) {
        Map<String, Object> context = AppContext.get();
        if (context != null && context.get(AuthConstant.USER_ID) != null) {
            int userId = (int) context.get(AuthConstant.USER_ID);
            dealIsLikedInfoForIllustList(illustrationList, userId);
        }
    }

    public void dealIsLikedInfoForIllustList(List<Illustration> illustrationList, int userId) {
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

    //@Scheduled(cron = "0 0 16 * * ?")
    @Transactional
    public void flushBookmarkCountToDb() {
        //半夜三点往mysql更新收藏数
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(RedisKeyConstant.BOOKMARK_COUNT_MAP_REDIS_PRE);
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            int illustId = Integer.parseInt(entry.getKey().toString());
            int increment = Integer.parseInt(entry.getValue().toString());
            businessMapper.updateIllustBookmark(illustId, increment);
        }
        stringRedisTemplate.delete(RedisKeyConstant.BOOKMARK_COUNT_MAP_REDIS_PRE);
    }

    public List<Illustration> queryBookmarked(int userId, String type, int currIndex, int pageSize) {
        return businessMapper.queryBookmarked(userId, type, currIndex, pageSize);
    }

    public Boolean queryIsBookmarked(int userId, Integer illustId) {
        return stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.BOOKMARK_REDIS_PRE + userId, String.valueOf(illustId));
    }

    @Caching(evict = {
            @CacheEvict(value = "followedLatest", key = "#userId + 'illust'"),
            @CacheEvict(value = "followedLatest", key = "#userId + 'manga'"),
            @CacheEvict(value = "artist_followed", key = "#artistId+'1'+'30'")})
    @Transactional
    public void follow(int userId, int artistId, String username) {
        try {
            businessMapper.follow(userId, artistId, username, LocalDateTime.now());
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "重复收藏");
        }
        stringRedisTemplate.opsForSet().add(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + artistId, String.valueOf(userId));
    }

    @Caching(evict = {
            @CacheEvict(value = "followedLatest", key = "#userId + 'illust'"),
            @CacheEvict(value = "followedLatest", key = "#userId + 'manga'")})
    public void cancelFollow(int userId, int artistId) {
        int effectRow = businessMapper.cancelFollow(userId, artistId);
        stringRedisTemplate.opsForSet().remove(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + artistId, String.valueOf(userId));
        if (effectRow == 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户画师关系请求错误");
        }
    }

    public List<Artist> queryFollowed(int userId, int currIndex, int pageSize) {
        List<Artist> artists = businessMapper.queryFollowed(userId, currIndex, pageSize);
        if (artists.size() != 0) {
            if (AppContext.get() != null && AppContext.get().get(AuthConstant.USER_ID) != null) {
                int user = (int) AppContext.get().get(AuthConstant.USER_ID);
                List<Object> isFollowedList;
                if (user == userId) {
                    isFollowedList = artists.stream().map(e -> true).collect(Collectors.toList());
                } else {
                    isFollowedList = stringRedisTemplate.executePipelined((RedisCallback<String>) redisConnection -> {
                        for (Artist artist : artists) {
                            StringRedisConnection stringRedisConnection = (StringRedisConnection) redisConnection;
                            stringRedisConnection.sIsMember(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + artist.getId(), String.valueOf(user));
                        }
                        return null;
                    });
                }
                for (int i = 0; i < artists.size(); i++) {
                    illustrationBizService.dealArtist(artists.get(i));
                    artists.set(i, new ArtistWithIsFollowedInfo(artists.get(i), (Boolean) isFollowedList.get(i)));
                }
            }
        }
        return artists;
    }

    public Boolean queryIsFollowed(int userId, Integer artistId) {
        return stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + artistId, String.valueOf(userId));
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

    public List<Illustration> queryFollowedLatest(int userId, String type, int page, int pageSize) {
        List<Integer> illustIdList = queryFollowedLatestSortedIllustId(userId, type).stream().skip(pageSize * (page - 1))
                .limit(pageSize).collect(Collectors.toList());
        List<Illustration> illustrations = null;
        if (illustIdList.size() != 0) {
            illustrations = queryIllustrationByIllustIdList(illustIdList);
        }
        return illustrations;
    }

    public List<Illustration> queryIllustrationByIllustIdList(List<Integer> illustIdList) {
        return illustIdList.stream().parallel().map(illustrationBizService::queryIllustrationById).collect(Collectors.toList());
    }

    @Cacheable(value = "followedLatest", key = "#userId+#type")
    public List<Integer> queryFollowedLatestSortedIllustId(int userId, String type) {
        //取出最近一个月关注画师的画作id
        List<Integer> illustrationIdList = businessMapper.queryFollowedLatestIllustId(userId, type);
        //遍历切割出k个升序数组,用大根堆进行合并得到TOP3000(最多3000,多了业务上没有意义)
        List<Integer> sortedIdList = mergekSortedArrays(split(illustrationIdList));
        return sortedIdList;
    }

    private static List<List<Integer>> split(List<Integer> illustrationIdList) {
        List<List<Integer>> result = new ArrayList<>();
        int size = illustrationIdList.size();
        if (size > 1) {
            int from = 0;
            int to = 1;
            for (; to < size; to++) {
                if (to == size - 1) {
                    result.add(Lists.reverse(illustrationIdList.subList(from, to + 1)));
                    break;
                } else if (to != size - 1 && illustrationIdList.get(to) > illustrationIdList.get(to + 1)) {
                    result.add(Lists.reverse(illustrationIdList.subList(from, to + 1)));
                    from = to + 1;
                }
            }
        }
        return result;
    }

    public List<ArtistWithRecentlyIllusts> queryFollowedWithRecentlyIllusts(Integer userId, int currIndex, int pageSize) {
        List<Artist> artists = queryFollowed(userId, currIndex, pageSize);
        return artists.stream().map(e -> {
            List<Illustration> illustrations = illustrationBizService.queryIllustrationsByArtistId(e.getId(), "illust", 0, 3);
            dealIsLikedInfoForIllustList(illustrations);
            return new ArtistWithRecentlyIllusts(e, illustrations);
        }).collect(Collectors.toList());
    }

    private static class NewInteger {
        int value, row, col;

        public NewInteger(int value, int row, int col) {
            this.value = value;
            this.row = row;
            this.col = col;
        }
    }

    public static List<Integer> mergekSortedArrays(List<List<Integer>> arrays) {
        ArrayList<Integer> list = new ArrayList<>();
        if (arrays == null || arrays.size() == 0 || arrays.get(0).size() == 0) {
            return list;
        }
        PriorityQueue<NewInteger> pq = new PriorityQueue<>(arrays.size(), (x, y) -> x.value > y.value ? -1 : 1);
        for (int i = 0; i < arrays.size(); i++) {
            pq.offer(new NewInteger(arrays.get(i).get(0), i, 0));
        }
        while (list.size() < 3000 && !pq.isEmpty()) {
            NewInteger min = pq.poll();
            if (min.col + 1 < arrays.get(min.row).size()) {
                pq.offer(new NewInteger(arrays.get(min.row).get(min.col + 1), min.row, min.col + 1));
            }
            list.add(min.value);
        }
        return list;
    }

}
