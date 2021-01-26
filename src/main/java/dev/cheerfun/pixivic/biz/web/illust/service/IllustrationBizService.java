package dev.cheerfun.pixivic.biz.web.illust.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.service.IllustrationService;
import dev.cheerfun.pixivic.biz.userInfo.dto.ArtistPreViewWithFollowedInfo;
import dev.cheerfun.pixivic.biz.userInfo.dto.IllustrationWithLikeInfo;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.illust.po.IllustRelated;
import dev.cheerfun.pixivic.biz.web.illust.secmapper.IllustrationBizMapper;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import dev.cheerfun.pixivic.common.util.translate.service.YouDaoTranslatedUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    private final ObjectMapper objectMapper;
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
                    if (!stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ILLUST_NOT_IN_PIXIV, String.valueOf(illustId))) {
                        Illustration illustration = illustrationService.pullIllustrationInfo(illustId);
                        if (illustration != null) {
                            List<Illustration> illustrations = Collections.singletonList(illustration);
                            illustrationService.saveToDb(illustrations);
                            log.info("获取画作：" + illustId + "完毕");
                        } else {
                            log.info("画作：" + illustId + "在pixiv上不存在");
                            stringRedisTemplate.opsForSet().add(RedisKeyConstant.ILLUST_NOT_IN_PIXIV, String.valueOf(illustId));
                        }
                    }
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

    public Illustration queryIllustrationById(Integer illustId) {
        Illustration illustration = queryIllustrationByIdFromDb(illustId);
        if (illustration == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "画作不存在或为限制级图片");
        }
        return illustration;
    }

    public List<Illustration> queryIllustrationByIdList(List<Integer> illustId) {
        return illustId.stream().parallel().map(this::queryIllustrationByIdFromDb).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Cacheable(value = "illust")
    @Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager = "SecondaryTransactionManager")
    public Illustration queryIllustrationByIdFromDb(Integer illustId) {
        //判断是否在封禁集合中
        if (stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.BLOCK_ILLUSTS_SET, String.valueOf(illustId))) {
            return null;
        }
        Illustration illustration = illustrationBizMapper.queryIllustrationByIllustId(illustId);
        if (illustration == null) {
            //log.info("画作：" + illustId + "不存在，加入队列等待爬取");
            return null;
            //waitForPullIllustQueue.offer(illustId);
        }
        if (illustration.getSanityLevel() > 2 || illustration.getTotalBookmarks() < 150) {
            return null;
        }
        return objectMapper.convertValue(illustration, new TypeReference<Illustration>() {
        });
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager = "SecondaryTransactionManager")
    public Illustration queryIllustrationByIdFromDbWithoutCache(Integer illustId) {
        Illustration illustration = illustrationBizMapper.queryIllustrationByIllustId(illustId);
        return objectMapper.convertValue(illustration, new TypeReference<Illustration>() {
        });
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager = "SecondaryTransactionManager")
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

    public int updateIllustBookmark(int illustId, int increment) {
        return illustrationBizMapper.updateIllustBookmark(illustId, increment);
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

    public Boolean queryExistsById(Integer id) {
        return queryIllustrationById(id) != null;
    }

    public List<Illustration> queryIllustrationByIllustIdList(List<Integer> illustIdList) {
        return illustIdList.stream().parallel().map(e -> {
            Illustration illustration = null;
            try {
                illustration = queryIllustrationById(e);
            } catch (BusinessException businessException) {
                //System.out.println("部分画作不存在" + e);
            }
            return illustration;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void dealIsLikedInfoForIllustList(List<Illustration> illustrationList) {
        Map<String, Object> context = AppContext.get();
        if (context != null && context.get(AuthConstant.USER_ID) != null) {
            int userId = (int) context.get(AuthConstant.USER_ID);
            dealIsLikedInfoForIllustList(illustrationList, userId);
        }
    }

    public void dealIsLikedInfoForIllustList(List<Illustration> illustrationList, int userId) {
        int size = illustrationList.size();
        for (int i = 0; i < size; i++) {
            IllustrationWithLikeInfo illustrationWithLikeInfo = new IllustrationWithLikeInfo(illustrationList.get(i), true);
            Boolean isFollow = stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + illustrationList.get(i).getArtistId(), String.valueOf(userId));
            illustrationWithLikeInfo.setArtistPreView(new ArtistPreViewWithFollowedInfo(illustrationWithLikeInfo.getArtistPreView(), isFollow));
            illustrationList.set(i, illustrationWithLikeInfo);
        }
    }

    public Integer getIllustType(Integer illustId) {
        String type = queryIllustrationById(illustId).getType();
        switch (type) {
            case "illust":
                return 1;
            case "manga":
                return 2;
            default:
                return 3;
        }
    }

}
