package dev.cheerfun.pixivic.biz.crawler.pixiv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/01 9:35
 * @description RankDTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IllustsDTO {
    private List<IllustrationDTO> illusts;
}
