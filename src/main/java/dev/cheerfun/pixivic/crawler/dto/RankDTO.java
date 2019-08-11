package dev.cheerfun.pixivic.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/01 9:35
 * @description RankDTO
 */
@Data
@AllArgsConstructor
public class RankDTO {
    private ArrayList<IllustrationDTO> illusts;
}
