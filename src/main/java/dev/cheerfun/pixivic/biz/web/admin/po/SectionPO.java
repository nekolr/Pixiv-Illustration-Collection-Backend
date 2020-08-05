package dev.cheerfun.pixivic.biz.web.admin.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

@Entity
@Table(name = "discussion_section")
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;
    @Column(name = "use_flag")
    private Integer useFlag;

    @Override
    public String toString() {
        return "SectionPO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", slogan='" + slogan + '\'' +
                ", discussionCount=" + discussionCount +
                ", createTime=" + createTime +
                ", useFlag=" + useFlag +
                '}';
    }

    public SectionPO(String name) {
        this.name = name;
    }

    public SectionPO() {
    }

    public SectionPO(Integer id, String desc, String slogan, Integer discussionCount, LocalDateTime createTime, Integer useFlag) {
        this.id = id;
        this.desc = desc;
        this.slogan = slogan;
        this.discussionCount = discussionCount;
        this.createTime = createTime;
        this.useFlag = useFlag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public Integer getDiscussionCount() {
        return discussionCount;
    }

    public void setDiscussionCount(Integer discussionCount) {
        this.discussionCount = discussionCount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getUseFlag() {
        return useFlag;
    }

    public void setUseFlag(Integer useFlag) {
        this.useFlag = useFlag;
    }
}
