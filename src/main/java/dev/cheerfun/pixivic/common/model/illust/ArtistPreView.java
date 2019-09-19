package dev.cheerfun.pixivic.common.model.illust;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/19 21:13
 * @description ArtistPreView
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistPreView {
    private Integer id;
    private String name;
    private String account;
    private String avatar;
}
