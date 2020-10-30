package dev.cheerfun.pixivic.biz.credit.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/26 3:26 下午
 * @description CreditHistory
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditHistory {
    private Integer id;
    private Integer userId;
    private String objectType;
    private Integer objectId;
    private String action;
    private Integer creditOption;
    private Integer creditScore;
    private String creditDesc;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createTime;
}
