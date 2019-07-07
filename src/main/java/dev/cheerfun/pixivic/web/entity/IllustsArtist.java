package dev.cheerfun.pixivic.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class IllustsArtist implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键id
     */
    @TableId(value = "artist_id", type = IdType.AUTO)
    private Long artistId;

    /**
     * 画师名字
     */
    private String name;

    /**
     * 账户
     */
    private String account;

    /**
     * 画师头像只有一张图，把URL截出来
     */
    private String profileImageUrl;

    /**
     * 个性签名
     */
    private String comment;

    private Integer isFollowed;


}
