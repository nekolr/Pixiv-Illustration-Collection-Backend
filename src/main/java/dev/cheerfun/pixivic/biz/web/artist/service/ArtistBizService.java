package dev.cheerfun.pixivic.biz.web.artist.service;

import dev.cheerfun.pixivic.biz.crawler.pixiv.service.ArtistService;
import dev.cheerfun.pixivic.biz.userInfo.dto.ArtistWithIsFollowedInfo;
import dev.cheerfun.pixivic.biz.web.artist.dto.ArtistSearchDTO;
import dev.cheerfun.pixivic.biz.web.artist.secmapper.ArtistBizMapper;
import dev.cheerfun.pixivic.biz.web.artist.util.ArtistSearchUtil;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.biz.web.user.dto.ArtistWithRecentlyIllusts;
import dev.cheerfun.pixivic.biz.web.user.dto.UserListDTO;
import dev.cheerfun.pixivic.biz.web.user.mapper.BusinessMapper;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.ArtistSummary;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/19 10:46 上午
 * @description ArtistBizService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArtistBizService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ArtistBizMapper artistBizMapper;
    private final BusinessMapper businessMapper;
    private final ArtistService artistService;
    private final ExecutorService crawlerExecutorService;
    private final IllustrationBizService illustrationBizService;
    private final ArtistSearchUtil artistSearchUtil;
    private final CacheManager cacheManager;
    private LinkedBlockingQueue<String> waitForPullArtistQueue;
    private LinkedBlockingQueue<Integer> waitForPullArtistInfoQueue;

    private volatile String today;
    private volatile String yesterday;

    {
        LocalDate now = LocalDate.now();
        today = now.toString();
        yesterday = now.plusDays(-1).toString();
        waitForPullArtistQueue = new LinkedBlockingQueue<>(100 * 1000);
        waitForPullArtistInfoQueue = new LinkedBlockingQueue<>(100 * 1000);
    }

    @PostConstruct
    public void init() {
        try {
            log.info("开始初始化画师基础服务");
            dealWaitForPullArtistQueue();
            dealWaitForPullArtistInfoQueue();
        } catch (Exception e) {
            log.error("初始化画师基础服务失败");
            e.printStackTrace();
        }
        log.info("初始化画师基础服务成功");

    }

    @Scheduled(cron = "0 2 0 * * ?")
    public void clearArtistLatestIllustsMap() {
        stringRedisTemplate.delete(RedisKeyConstant.ARTIST_LATEST_ILLUSTS_PULL_FLAG + yesterday);
        yesterday = today;
        today = LocalDate.now().toString();
    }

    public Artist queryArtistDetail(Integer artistId) throws InterruptedException {
        Artist artist = queryArtistById(artistId);
        if (artist == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "画师不存在");
        }
        dealArtist(artist);
        Map<String, Object> context = AppContext.get();
        if (context != null && context.get(AuthConstant.USER_ID) != null) {
            int userId = (int) context.get(AuthConstant.USER_ID);
            Boolean isFollowed = stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + artistId, String.valueOf(userId));
            return new ArtistWithIsFollowedInfo(artist, isFollowed);
        }
        return artist;
    }

    public Artist queryArtistDetail(Integer artistId, Integer userId) throws InterruptedException {
        Artist artist = queryArtistById(artistId);
        dealArtist(artist);
        if (userId != null) {
            Boolean isFollowed = stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + artistId, String.valueOf(userId));
            return new ArtistWithIsFollowedInfo(artist, isFollowed);
        }
        return artist;
    }

    public void dealArtist(Artist artist) {
        //更改关注数
        artist.setTotalFollowUsers(String.valueOf(stringRedisTemplate.opsForSet().size(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + artist.getId())));
    }

    public Artist queryArtistById(Integer artistId) {
        if (stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.BLOCK_ARTISTS_SET, String.valueOf(artistId))) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "画师不存在");
        }
        Artist artist = queryArtistByIdFromDb(artistId);
        return artist;
    }

    public List<Artist> queryArtistByIdList(List<Integer> artistIdList) {
        return artistIdList.stream().parallel().map(e -> queryArtistByIdFromDb(e)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Cacheable(value = "artist")
    @Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager = "SecondaryTransactionManager")
    public Artist queryArtistByIdFromDb(Integer artistId) {
        if (stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.BLOCK_ARTISTS_SET, String.valueOf(artistId))) {
            return null;
        }
        Artist artist = artistBizMapper.queryArtistById(artistId);
        if (artist == null) {
            waitForPullArtistInfoQueue.offer(artistId);
        }
        return artist;
    }

    @Cacheable("artist_followed")
    public List<UserListDTO> queryUserListFollowedArtist(Integer artistId, Integer page, Integer pageSize) {
        return businessMapper.queryUserListFollowedArtist(artistId, (page - 1) * pageSize, pageSize);
    }

    @Cacheable(value = "artist_illusts")
    public List<Illustration> queryIllustrationsByArtistId(Integer artistId, String type, int currIndex, int pageSize) throws InterruptedException {
        //如果是近日首次则进行拉取
        List<Integer> illustrations = artistBizMapper.queryIllustrationsByArtistId(artistId, type, currIndex, pageSize);
        String key = artistId + ":" + type;
        //防止抓取不在库内的画师画作
        if (currIndex == 0 && pageSize == 30 && illustrations.size() > 0) {
            waitForPullArtistQueue.offer(key);
        }
        return illustrationBizService.queryIllustrationByIdList(illustrations);
    }

    public void dealWaitForPullArtistQueue() {
        crawlerExecutorService.submit(() -> {
            while (true) {
                //处理画师画作
                String key = null;
                //处理画师详情
                try {
                    key = waitForPullArtistQueue.take();
                    Boolean todayCheck = stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_LATEST_ILLUSTS_PULL_FLAG + today, key);
                    Boolean yesterdayCheck = stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_LATEST_ILLUSTS_PULL_FLAG + yesterday, key);
                    if (!(todayCheck || yesterdayCheck)) {
                        String[] split = key.split(":");
                        System.out.println("开始从Pixiv获取画师(id:" + split[0] + ")首页画作");
                        stringRedisTemplate.opsForSet().add(RedisKeyConstant.ARTIST_LATEST_ILLUSTS_PULL_FLAG + today, key);
                        artistService.pullArtistLatestIllust(Integer.valueOf(split[0]), split[1]);
                        log.info("获取画师(id:" + split[0] + ")首页画作完毕");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void dealWaitForPullArtistInfoQueue() {
        crawlerExecutorService.submit(() -> {
            while (true) {
                Integer artistId = null;
                try {
                    artistId = waitForPullArtistInfoQueue.take();
                    if (!stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_NOT_IN_PIXIV, String.valueOf(artistId))) {
                        log.info("开始从Pixiv获取画师(id:" + artistId + ")信息");
                        Artist artist = artistService.pullArtistsInfo(artistId);
                        if (artist != null) {
                            log.info("获取画师(id:" + artistId + ")信息完毕");
                            putArtistCache(artist);
                            artistService.pullArtistAllIllust(artistId);
                        } else {
                            log.info("画师(id:" + artistId + ")信息在Pixiv上不存在");
                            stringRedisTemplate.opsForSet().add(RedisKeyConstant.ARTIST_NOT_IN_PIXIV, String.valueOf(artistId));
                        }
                    } else {
                        log.info("画师(id:" + artistId + ")信息在Pixiv上不存在，跳过");
                    }
                } catch (Exception exception) {
                    log.error("获取画师(id:" + artistId + ")信息失败");
                    exception.printStackTrace();
                }
            }
        });
    }

    public void putArtistCache(Artist artist) {
        cacheManager.getCache("artist").put(artist.getId(), artist);
    }

    @Cacheable(value = "artistSummarys")
    public ArtistSummary querySummaryByArtistId(Integer artistId) {
        return artistBizMapper.querySummaryByArtistId(artistId);
    }

    public CompletableFuture<List<Artist>> queryArtistByName(String artistName, Integer page, Integer pageSize) {
        Map<String, Object> context = AppContext.get();
        Integer userId = null;
        if (context != null && context.get(AuthConstant.USER_ID) != null) {
            userId = (int) context.get(AuthConstant.USER_ID);
        }
        CompletableFuture<List<ArtistSearchDTO>> request = artistSearchUtil.search(artistName, page, pageSize);
        Integer finalUserId = userId;
        return request.thenApply(e -> e.stream().parallel().filter(Objects::nonNull).map(artistSearchDTO ->
        {
            List<Illustration> illustrations = null;
            try {
                illustrations = queryIllustrationsByArtistId(artistSearchDTO.getId(), "illust", 0, 3);
                return new ArtistWithRecentlyIllusts(queryArtistDetail(artistSearchDTO.getId(), finalUserId), illustrations);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public Boolean pullArtistAllIllust(Integer artistId) {
        artistService.pullArtistAllIllust(artistId);
        return true;
    }

    public Boolean queryExistsById(Integer id) {
        return queryArtistById(id) != null;
    }
}
