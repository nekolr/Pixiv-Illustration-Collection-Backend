package dev.cheerfun.pixivic.biz.web.illust.service;

import dev.cheerfun.pixivic.biz.crawler.pixiv.service.IllustrationService;
import dev.cheerfun.pixivic.biz.userInfo.dto.ArtistPreViewWithFollowedInfo;
import dev.cheerfun.pixivic.biz.userInfo.dto.IllustrationWithLikeInfo;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.common.util.YouDaoTranslatedUtil;
import dev.cheerfun.pixivic.biz.web.illust.mapper.IllustrationBizMapper;
import dev.cheerfun.pixivic.biz.web.illust.po.IllustRelated;
import dev.cheerfun.pixivic.biz.web.user.dto.UserListDTO;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/28 16:10
 * @description IllustrationService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IllustrationBizService {
    private static volatile ConcurrentHashMap<String, List<Illustration>> waitSaveToDb = new ConcurrentHashMap(10000);
    private final IllustrationBizMapper illustrationBizMapper;
    private final IllustrationService illustrationService;
    private final StringRedisTemplate stringRedisTemplate;
    private LinkedBlockingQueue<Integer> waitForPullIllustQueue;
    private final ExecutorService crawlerExecutorService;

    @PostConstruct
    public void init() {
        waitForPullIllustQueue = new LinkedBlockingQueue<>(1000 * 1000);
        dealWaitForPullIllustQueue();
    }

    public void dealWaitForPullIllustQueue() {
        crawlerExecutorService.submit(() -> {
            while (true) {
                Integer illustId;
                try {
                    illustId = waitForPullIllustQueue.take();
                    log.info("开始从pixiv获取画作：" + illustId);
                    Illustration illustration = illustrationService.pullIllustrationInfo(illustId);
                    if (illustration == null) {
                        throw new BusinessException(HttpStatus.NOT_FOUND, "画作不存在或为限制级图片");
                    } else {
                        List<Illustration> illustrations = new ArrayList<>(1);
                        illustrations.add(illustration);
                        illustrationService.saveToDb(illustrations);
                    }
                    log.info("获取画作：" + illustId + "完毕");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Cacheable(value = "tagTranslation")
    public Tag translationTag(String tag) {
        return new Tag(tag, YouDaoTranslatedUtil.truncate(tag));
    }

    public Illustration queryIllustrationByIdWithUserInfo(Integer illustId) {
        Illustration illustration = queryIllustrationById(illustId);
        Map<String, Object> context = AppContext.get();
        if (context != null && context.get(AuthConstant.USER_ID) != null) {
            int userId = (int) context.get(AuthConstant.USER_ID);
            //log.info("用户:" + userId + "开始获取画作:" + illustId);
            Boolean isBookmarked = stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.BOOKMARK_REDIS_PRE + userId, String.valueOf(illustId));
            //businessService.queryIsBookmarked(userId, illustId);
            illustration = new IllustrationWithLikeInfo(illustration, isBookmarked);
            Boolean isFollowed = stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + illustration.getArtistId(), String.valueOf(userId));
            //businessService.queryIsFollowed(userId, illustration.getArtistId());
            illustration.setArtistPreView(new ArtistPreViewWithFollowedInfo(illustration.getArtistPreView(), isFollowed));
        }
        return illustration;
    }

    @Cacheable(value = "illust")
    public Illustration queryIllustrationById(Integer illustId) {
        Illustration illustration = illustrationBizMapper.queryIllustrationByIllustId(illustId);
        if (illustration == null) {
            log.info("画作：" + illustId + "不存在，加入队列等待爬取");
            waitForPullIllustQueue.offer(illustId);
            throw new BusinessException(HttpStatus.NOT_FOUND, "画作不存在或为限制级图片");
            /* */
        }
        return illustration;
    }

    public String queryRandomIllustration(String urlType, String illustType, Boolean detail, String ratio, Float range, Integer maxSanityLevel) {
        String[] split = ratio.split(":");
        float r = Float.parseFloat(split[0]) / Float.parseFloat(split[1]);
        float minR = r - range;
        float maxR = r + range;
        List<Illustration> illustrations = illustrationBizMapper.queryRandomIllustration().stream().sorted(Comparator.comparingInt(i -> -i.getTotalBookmarks())).collect(Collectors.toList());
        Illustration illustration = illustrations.stream().takeWhile(i -> {
            float w_h = (float) i.getWidth() / i.getHeight();
            return illustType.equals(i.getType()) && w_h >= minR && w_h <= maxR && i.getSanityLevel() <= maxSanityLevel;
        }).findAny().orElse(illustrations.get(0));
        Map<String, String> imageUrl = (Map<String, String>) illustration.getImageUrls().get(0);
        StringBuilder url;
        url = new StringBuilder(imageUrl.get(urlType).replace("i.pximg.net", "i.pximg.qixiv.me"));
        if (detail) {
            url.append("?title=").append(URLEncoder.encode(illustration.getTitle(), StandardCharsets.UTF_8))
                    .append("&id=").append(illustration.getId())
                    .append("&artistName=").append(URLEncoder.encode(illustration.getArtistPreView().getName(), StandardCharsets.UTF_8))
                    .append("&artistId=").append(illustration.getArtistId());
        }
        return url.toString();
    }

    //@Scheduled(cron = "0 0/5 * * * ? ")
    void saveIllustRelatedToDb() {
        final HashMap<String, List<Illustration>> temp = new HashMap<>(waitSaveToDb);
        waitSaveToDb.clear();
        //持久化
        if (!temp.isEmpty()) {
            List<IllustRelated> illustRelatedList = new ArrayList<>(2000);
            List<Illustration> illustrationList = temp.keySet().stream().map(e -> {
                String[] split = e.split(":");
                int illustId = Integer.parseInt(split[0]);
                int page = Integer.parseInt(split[1]);
                List<Illustration> illustrations = temp.get(e);
                int size = illustrations.size();
                for (int i = 0; i < size; i++) {
                    illustRelatedList.add(new IllustRelated(illustId, illustrations.get(i).getId(), (page - 1) * 30 + i));
                }
                return illustrations;
            }).flatMap(Collection::stream).collect(Collectors.toList());
            //先更新画作
            illustrationService.saveToDb(illustrationList);
            //插入联系
            illustrationBizMapper.insertIllustRelated(illustRelatedList);
        }

    }

    public Boolean queryExistsById(String type, Integer id) {
        if ("illust".equals(type)) {
            return queryIllustrationById(id) != null;
        }
        return false;
    }

    public List<Illustration> queryIllustrationByIllustIdList(List<Integer> illustIdList) {
        return illustIdList.stream().map(e -> {
            Illustration illustration = null;
            try {
                illustration = queryIllustrationById(e);
            } catch (BusinessException businessException) {
                System.out.println("部分画作不存在");
            }
            return illustration;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Cacheable("illust_bookmarked")
    public List<UserListDTO> queryUserListBookmarkedIllust(Integer illustId, Integer page, Integer pageSize) {
        return illustrationBizMapper.queryUserListBookmarkedIllust(illustId, (page - 1) * pageSize, pageSize);
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

}
