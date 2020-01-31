package dev.cheerfun.pixivic.biz.web.common.dto;

import dev.cheerfun.pixivic.common.po.illust.ArtistPreView;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/1/31 下午7:55
 * @description ArtistPreViewWithFollowedInfo
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ArtistPreViewWithFollowedInfo extends ArtistPreView {
    private Boolean isFollowed;

    public ArtistPreViewWithFollowedInfo(ArtistPreView artistPreView, Boolean isFollowed) {
        this.isFollowed = isFollowed;
        this.id = artistPreView.getId();
        this.name = artistPreView.getName();
        this.account = artistPreView.getAccount();
        this.avatar = artistPreView.getAvatar();
    }

    public ArtistPreViewWithFollowedInfo(Integer id, String name, String account, String avatar, Boolean isFollowed) {
        super(id, name, account, avatar);
        this.isFollowed = isFollowed;
    }
}
