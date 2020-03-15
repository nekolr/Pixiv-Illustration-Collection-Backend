package dev.cheerfun.pixivic.biz.web.user.dto;

import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/15 12:12 上午
 * @description AtristWithRecentlyIllusts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistWithRecentlyIllusts {
    private Artist artist;
    private List<Illustration> illustrations;
}
