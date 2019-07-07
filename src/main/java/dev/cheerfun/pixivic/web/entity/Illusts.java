package dev.cheerfun.pixivic.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 插画表
 * </p>
 *
 * @author huangsm
 * @since 2019-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Illusts implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键id
     */
    @TableId(value = "illusts_id", type = IdType.AUTO)
    private Long illustsId;

    /**
     * 标题
     */
    private String title;

    /**
     * 1:ugoira、2:manga、3：illust
     */
    private Integer type;

    /**
     * 附言
     */
    private String caption;

    /**
     * 限制
     */
    private Integer restrict;

    /**
     * 画师ID，逻辑外键
     */
    private Long artistId;

    /**
     * 作画工具,数组形式用,分割
     */
    private String tools;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 插花总数
     */
    private Long pageCount;

    /**
     * 宽度
     */
    private Integer width;

    /**
     * 高度
     */
    private Integer height;

    /**
     * 情色级别
     */
    private Integer sanityLevel;

    /**
     * 十八禁限制
     */
    private Integer xRestrict;

    /**
     * 查看数
     */
    private Long totalView;

    /**
     * 收藏数
     */
    private Long totalBookmarks;

    /**
     * 评论数
     */
    private Long totalComments;


}
