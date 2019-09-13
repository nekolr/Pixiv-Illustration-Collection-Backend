package dev.cheerfun.pixivic.web.rank.service;

import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.web.rank.mapper.RankMapper;
import dev.cheerfun.pixivic.web.rank.model.Rank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/12 17:01
 * @description RankService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RankService {
    private final RankMapper rankMapper;

    public boolean insert(Rank rank) {
        return rankMapper.insert(rank)==1;
    }

    public Rank queryByDateAndMode(String date, String mode, int page, int pageSize) {
        Rank rank = rankMapper.queryByDateAndMode(date, mode);
        List<Illustration> illustrations = rank.getIllustrations().subList(page * pageSize, (page + 1) * pageSize);
        rank.setIllustrations(illustrations);
        return rank;
    }
}
