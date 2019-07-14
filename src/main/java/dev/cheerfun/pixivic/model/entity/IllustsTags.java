package dev.cheerfun.pixivic.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 画师简单信息
 * </p>
 *
 * @author huangsm
 * @since 2019-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class IllustsTags implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键id
     */
    @TableId(value = "tags_id", type = IdType.AUTO)
    private Long tagsId;

    /**
     * 插画id
     */
    private String illustsId;

    /**
     * 名字
     */
    private String name;

    /**
     * 中文名字
     */
    private String translatedName;


}
