package dev.cheerfun.pixivic.biz.web.collection.po;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ImageUrl;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/29 5:22 下午
 * @description Collection
 */
@Data
public class Collection {
    @JsonAlias({"collection_id", "collectionId"})
    private Integer id;
    private Integer userId;
    @NotBlank
    private String username;
    private List<ImageUrl> cover;
    @SensitiveCheck
    @Length(max = 100, min = 1, message = "标题长度不符合")
    private String title;
    @SensitiveCheck
    private String caption;
    @SensitiveCheck
    private List<CollectionTag> tagList;
    private Integer illustCount;
    private List<Illustration> illustrationList;
    private Integer isPublic;
    private Integer useFlag;
    private Integer forbidComment;
    private Integer pornWarning;
    private Integer totalBookmarked;
    private Integer totalView;
    private Integer totalPeopleSeen;
    private Integer totalLiked;
    private Integer totalReward;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createTime;

}
