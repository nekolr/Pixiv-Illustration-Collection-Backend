package dev.cheerfun.pixivic.biz.web.illust.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.service.ArtistService;
import dev.cheerfun.pixivic.biz.crawler.pixiv.service.IllustrationService;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.common.util.YouDaoTranslatedUtil;
import dev.cheerfun.pixivic.biz.web.illust.mapper.IllustrationBizMapper;
import dev.cheerfun.pixivic.biz.web.illust.po.IllustRelated;
import dev.cheerfun.pixivic.biz.web.search.domain.SearchResult;
import dev.cheerfun.pixivic.biz.web.search.service.SearchService;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.ArtistSummary;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    private final SearchService searchService;
    private final RequestUtil requestUtil;
    private static volatile ConcurrentHashMap<String, List<Illustration>> waitSaveToDb = new ConcurrentHashMap(10000);
    private final ObjectMapper objectMapper;

    @Cacheable(value = "tagTranslation")
    public Tag translationTag(String tag) {
        return new Tag(tag, YouDaoTranslatedUtil.truncate(tag));
    }

    @Cacheable(value = "illust")
    public List<Illustration> queryIllustrationsByArtistId(Integer artistId, String type, int currIndex, int pageSize, int maxSanityLevel) {
        List<Illustration> illustrations = illustrationBizMapper.queryIllustrationsByArtistId(artistId, type, currIndex, pageSize, maxSanityLevel);
        return illustrations;
    }

    @Cacheable(value = "artist")
    public Artist queryArtistById(Integer artistId) {
        Artist artist = illustrationBizMapper.queryArtistById(artistId);
        if (artist == null) {
            artist = artistService.pullArtistsInfo(artistId);
            if (artist == null) {
                throw new BusinessException(HttpStatus.NOT_FOUND, "画师不存在");
            }
            return artist;
        }
        return artist;
    }

    @Cacheable(value = "illust")
    public Illustration queryIllustrationById(Integer illustId/*, Integer xRestrict*/) {
        Illustration illustration = illustrationBizMapper.queryIllustrationByIllustId(illustId/*, xRestrict*/);
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
    public List<ArtistSummary> querySummaryByArtistId(Integer artistId) {
        return illustrationBizMapper.querySummaryByArtistId(artistId);
    }

/*
    @Cacheable(value = "illust")
    public CompletableFuture<List<Illustration>> queryIllustrationRelated(int illustId, int page) {
        return requestUtil.getJson("https://proxy.pixivic.com:23334/v2/illust/related?illust_id=" + illustId + "&offset=" + (page - 1) * 30).thenApply(r -> {
            try {
                IllustsDTO illustsDTO = objectMapper.readValue(r, new TypeReference<IllustsDTO>() {
                });
                if (illustsDTO.getIllusts() != null) {
                    List<Illustration> illustrationList = illustsDTO.getIllusts().stream().map(IllustrationDTO::castToIllustration).collect(Collectors.toList());
                    if (illustrationList.size() > 0) {
                        //保存
                        waitSaveToDb.put(illustId + ":" + page, illustrationList);
                    }
                    return illustrationList;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
*/

    @Cacheable(value = "illust")
    public CompletableFuture<List<Illustration>> queryIllustrationRelated(int illustId, int page, int pageSize) {
        Illustration illustration = queryIllustrationById(illustId);
        illustration=objectMapper.convertValue(illustration, new TypeReference<Illustration>() {
        });
        if (illustration != null && illustration.getTags().size() > 0) {
            String keywords = illustration.getTags().stream().map(Tag::getName).reduce((x, y) -> x + "||" + y).get();
            return searchService.searchByKeyword(keywords, pageSize, page, "original", "illust", 0, 0, "2008-01-01", "9999-12-31", 0, 0, 0, 0, 6).thenApply(SearchResult::getIllustrations);
        }
        throw new BusinessException(HttpStatus.NOT_FOUND, "画作不存在");
    }

    @Scheduled(cron = "0 0/5 * * * ? ")
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

    public void deal(Map<String, String> map) {
        map = null;
    }
}
