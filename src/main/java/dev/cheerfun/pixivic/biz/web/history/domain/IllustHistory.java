package dev.cheerfun.pixivic.biz.web.history.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/12 5:17 下午
 * @description IllustLog
 */
@Data
public class IllustHistory {
    private Integer userId;
    private Integer illustId;
    private LocalDateTime createAt;

    public IllustHistory() {
        createAt = LocalDateTime.now();
    }
}
