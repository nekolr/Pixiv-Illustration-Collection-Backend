package dev.cheerfun.pixivic.biz.notify.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-14 下午9:35
 * @description NotifyRemind
 */
@Data
@AllArgsConstructor
public class NotifyRemind {
    private Integer id;
    private Integer senderId;
    private String senderName;
    private String senderAction;
    private Integer objectId;
    private String objectType;
    private Integer recipientId;
    private String message;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    private LocalDateTime createDate;
    private Integer status;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
    private LocalDateTime readAt;
}
