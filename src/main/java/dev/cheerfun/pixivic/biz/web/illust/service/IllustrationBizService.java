package dev.cheerfun.pixivic.biz.web.illust.service;

import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.common.util.YouDaoTranslatedUtil;
import dev.cheerfun.pixivic.biz.web.illust.mapper.IllustrationBizMapper;
import dev.cheerfun.pixivic.common.model.Artist;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    public Tag translationTag(String tag) {
        return new Tag(tag, YouDaoTranslatedUtil.truncate(tag));
    }

    public List<Illustration> queryIllustrationsByArtistId(String artistId, int currIndex, int pageSize, int maxSanityLevel) {
        List<Illustration> illustrations = illustrationBizMapper.queryIllustrationsByArtistId(artistId, currIndex, pageSize,maxSanityLevel);
        return illustrations;
    }

    public Artist queryArtistById(String artistId) {
        Artist artist = illustrationBizMapper.queryArtistById(artistId);
        if (artist == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "画师不存在");
        }
        return artist;
    }

    public Illustration queryIllustrationById(String illustId) {
        Illustration illustration = illustrationBizMapper.queryIllustrationByIllustId(illustId);
        if (illustration == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "画作不存在");
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
}
