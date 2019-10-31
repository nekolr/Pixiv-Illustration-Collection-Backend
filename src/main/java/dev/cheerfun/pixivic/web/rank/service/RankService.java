package dev.cheerfun.pixivic.web.rank.service;

import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.web.rank.mapper.RankMapper;
import dev.cheerfun.pixivic.web.rank.model.Rank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public Rank queryByDateAndMode(String date, String mode, int page, int pageSize) {
        Rank rank = rankMapper.queryByDateAndMode(date, mode);
        List<Illustration> illustrations;
        if (rank != null) {
            int size = rank.getData().size();
            illustrations = rank.getData().stream().skip(pageSize * (page - 1))
                    .limit(pageSize).collect(Collectors.toList());
        } else {
            illustrations = new ArrayList<>();
        }
        rank.setData(illustrations);
        return rank;
    }
}
