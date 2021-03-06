package dev.cheerfun.pixivic.biz.notify.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 19-12-14 下午9:35
 * @description NotifyRemind
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyRemind {
    private Integer id;
    private Integer type;//用于点赞、收藏的合并处理
    private List<Actor> actors;
    private Integer actorCount;
    private String objectType;
    private Integer objectId;
    private String objectTitle;
    private Integer recipientId;
    private String message;
    private String extend;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createDate;
    private Integer status;

    public long createDateTimeStamp() {
        return createDate.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

}
