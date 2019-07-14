package dev.cheerfun.pixivic.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 画师简介表
 * </p>
 *
 * @author huangsm
 * @since 2019-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class IllustsArtistProfile implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键id
     */
    @TableId(value = "profile_id", type = IdType.AUTO)
    private Long profileId;

    /**
     * 画师id 逻辑外键
     */
    private Long artistId;

    /**
     * 微博链接
     */
    private String webpage;

    /**
     * 0：男、1：女
     */
    private Integer gender;

    /**
     * 地区
     */
    private String region;

    /**
     * 推特账户
     */
    private String twitterAccount;

    /**
     * 推特链接
     */
    private String twitterUrl;


}
