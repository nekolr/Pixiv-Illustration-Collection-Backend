package dev.cheerfun.pixivic.biz.recommend.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/8/11 10:16 PM
 * @description Feedback
 */
@Data
public class UREvent {
    private String eventId;
    private String event;
    private String entityType;
    private String entityId;
    private String targetEntityType;
    private String targetEntityId;
    private Map<String, Object> properties;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime eventTime;

    @Override
    public String toString() {
        return "UREvent{" +
                "eventId='" + eventId + '\'' +
                ", event='" + event + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", targetEntityType='" + targetEntityType + '\'' +
                ", targetEntityId='" + targetEntityId + '\'' +
                ", properties=" + properties +
                ", eventTime=" + eventTime +
                '}';
    }
}
