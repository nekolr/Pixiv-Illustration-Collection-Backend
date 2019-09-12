package dev.cheerfun.pixivic.web.rank.model;

import dev.cheerfun.pixivic.common.model.Illustration;
import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/12 14:27
 * @description Rank
 */
@Data
public class Rank {
    private String date;
    private String mode;
    private List<Illustration> data;
}
