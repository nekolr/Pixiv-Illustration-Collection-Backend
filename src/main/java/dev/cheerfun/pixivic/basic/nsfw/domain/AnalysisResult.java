package dev.cheerfun.pixivic.basic.nsfw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/25 6:02 下午
 * @description AnalysisResult
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisResult {
    private Integer drawings;
    private Integer hentai;
    private Integer neutral;
    private Integer porn;
    private Integer sexy;

    public AnalysisResult(float[] socre) {
        this.drawings = round(socre[0] * 100, 2);
        this.hentai = round(socre[1] * 100, 2);
        this.neutral = round(socre[2] * 100, 2);
        this.porn = round(socre[3] * 100, 2);
        this.sexy = round(socre[4] * 100, 2);
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
