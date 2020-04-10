package dev.cheerfun.pixivic.biz.web.illust.service;

import dev.cheerfun.pixivic.biz.crawler.pixiv.service.ArtistService;
import dev.cheerfun.pixivic.biz.crawler.pixiv.service.IllustrationService;
import dev.cheerfun.pixivic.biz.userInfo.dto.ArtistPreViewWithFollowedInfo;
import dev.cheerfun.pixivic.biz.userInfo.dto.ArtistWithIsFollowedInfo;
import dev.cheerfun.pixivic.biz.userInfo.dto.IllustrationWithLikeInfo;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.common.util.YouDaoTranslatedUtil;
import dev.cheerfun.pixivic.biz.web.illust.mapper.IllustrationBizMapper;
import dev.cheerfun.pixivic.biz.web.illust.po.IllustRelated;
import dev.cheerfun.pixivic.biz.web.user.dto.UserListDTO;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.ArtistSummary;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/28 16:10
 * @description IllustrationService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IllustrationBizService {
    private final IllustrationBizMapper illustrationBizMapper;
    private final IllustrationService illustrationService;
    private final ArtistService artistService;
    private final StringRedisTemplate stringRedisTemplate;
    private static volatile ConcurrentHashMap<String, List<Illustration>> waitSaveToDb = new ConcurrentHashMap(10000);
    private volatile String today;
    private volatile String yesterday;

    {
        LocalDate now = LocalDate.now();
        today = now.toString();
        yesterday = now.plusDays(-1).toString();
    }

    @Cacheable(value = "tagTranslation")
    public Tag translationTag(String tag) {
        return new Tag(tag, YouDaoTranslatedUtil.truncate(tag));
    }

    @Cacheable(value = "artist_illusts")
    public List<Illustration> queryIllustrationsByArtistId(Integer artistId, String type, int currIndex, int pageSize) {
        //如果是近日首次则进行拉取
        String key = artistId + ":" + type;
        Boolean todayCheck = stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_LATEST_ILLUSTS_PULL_FLAG + today, key);
        Boolean yesterdayCheck = stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_LATEST_ILLUSTS_PULL_FLAG + yesterday, key);
        if (currIndex == 0 && pageSize == 30 && !(todayCheck || yesterdayCheck)) {
            System.out.println("近日首次，将从Pixiv拉取");
            stringRedisTemplate.opsForSet().add(RedisKeyConstant.ARTIST_LATEST_ILLUSTS_PULL_FLAG + today, key);
            artistService.pullArtistLatestIllust(artistId, type);
        }
        List<Illustration> illustrations = illustrationBizMapper.queryIllustrationsByArtistId(artistId, type, currIndex, pageSize);
        return illustrations;
    }

    @Scheduled(cron = "0 1 0 * * ?")
    public void clearArtistLatestIllustsMap() {
        stringRedisTemplate.delete(RedisKeyConstant.ARTIST_LATEST_ILLUSTS_PULL_FLAG + yesterday);
        yesterday = today;
        today = LocalDate.now().toString();
    }

    public Artist queryArtistDetail(Integer artistId) {
        Artist artist = queryArtistById(artistId);
        dealArtist(artist);
        Map<String, Object> context = AppContext.get();
        if (context != null && context.get(AuthConstant.USER_ID) != null) {
            int userId = (int) context.get(AuthConstant.USER_ID);
            Boolean isFollowed = stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + artistId, String.valueOf(userId));
            //businessService.queryIsFollowed(userId, artist.getId());

            return new ArtistWithIsFollowedInfo(artist, isFollowed);
        }
        return artist;
    }

    public void dealArtist(Artist artist) {
        //更改关注数
        artist.setTotalFollowUsers(String.valueOf(stringRedisTemplate.opsForSet().size(RedisKeyConstant.ARTIST_FOLLOW_REDIS_PRE + artist.getId())));

    }

    @Cacheable(value = "artist")
    public Artist queryArtistById(Integer artistId) {
        Artist artist = illustrationBizMapper.queryArtistById(artistId);
        if (artist == null) {
            artist = artistService.pullArtistsInfo(artistId);
            if (artist == null) {
                throw new BusinessException(HttpStatus.NOT_FOUND, "画师不存在");
            }
        }

        return artist;
    }

    public Illustration queryIllustrationByIdWithUserInfo(Integer illustId) {
        Illustration illustration = queryIllustrationById(illustId);
        Map<String, Object> context = AppContext.get();
        if (context != null && context.get(AuthConstant.USER_ID) != null) {
            int userId = (int) context.get(AuthConstant.USER_ID);
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
            illustration = illustrationService.pullIllustrationInfo(illustId);
            if (illustration == null) {
                throw new BusinessException(HttpStatus.NOT_FOUND, "画作不存在或为限制级图片");
            } else {
                List<Illustration> illustrations = new ArrayList<>(1);
                illustrations.add(illustration);
                illustrationService.saveToDb(illustrations);
            }
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

    @Cacheable(value = "artistSummarys")
    public ArtistSummary querySummaryByArtistId(Integer artistId) {
        return illustrationBizMapper.querySummaryByArtistId(artistId);
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
        if ("artist".equals(type)) {
            return queryArtistById(id) != null;
        }
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

    @Cacheable("artist_followed")
    public List<UserListDTO> queryUserListFollowedArtist(Integer artistId, Integer page, Integer pageSize) {
        return illustrationBizMapper.queryUserListFollowedArtist(artistId, (page - 1) * pageSize, pageSize);
    }
}
