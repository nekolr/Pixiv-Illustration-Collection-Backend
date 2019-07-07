package dev.cheerfun.pixivic.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 插画图片url表
 * </p>
 *
 * @author huangsm
 * @since 2019-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class IllustsImageUrls implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键id
     */
    @TableId(value = "image_url_id", type = IdType.AUTO)
    private Long imageUrlId;

    /**
     * 插画id
     */
    private String illustsId;

    /**
     * 方形预览图
     */
    private String squareMedium;

    /**
     * 中图
     */
    private String medium;

    /**
     * 大图
     */
    private String large;

    /**
     * 原图
     */
    private String original;

    /**
     * 0:不是封面、1:是封面
     */
    private Integer isCover;


}
