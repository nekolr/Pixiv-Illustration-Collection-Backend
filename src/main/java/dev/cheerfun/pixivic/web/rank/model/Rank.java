package dev.cheerfun.pixivic.web.rank.model;

import dev.cheerfun.pixivic.common.model.Illustration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/17 17:41
 * @description Rank
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rank {
    private List<Illustration> data;
    private String mode;
    private String date;
}
