package dev.cheerfun.pixivic.biz.web.admin.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/29 1:50 下午
 * @description Section
 */
@Data
@Entity(name = "discussion_section")
public class SectionPO {
    @Id
    @Column(name = "section_id")
    private Integer id;
    private String name;
    private String desc;
    private String slogan;
    @Column(name = "discussion_count")
    private Integer discussionCount;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "create_time")
    private LocalDateTime createTime;
    @Column(name = "use_flag")
    private Integer useFlag;

}
