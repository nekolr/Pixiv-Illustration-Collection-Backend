package dev.cheerfun.pixivic.web.illust.service;

import dev.cheerfun.pixivic.common.model.Artist;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import dev.cheerfun.pixivic.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.web.common.util.YouDaoTranslatedUtil;
import dev.cheerfun.pixivic.web.illust.mapper.IllustrationBizMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Illustration> queryIllustrationsByArtistId(String artistId, int currIndex, int pageSize) {
        return illustrationBizMapper.queryIllustrationsByArtistId(artistId, currIndex, pageSize);
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
}
