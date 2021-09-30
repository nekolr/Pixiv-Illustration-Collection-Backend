package dev.cheerfun.pixivic.biz.web.artist.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/19 12:35 下午
 * @description ArtistSearchDTO
 */
@Data
public class ArtistSearchDTO {
    @JsonAlias({"artist_id", "id"})
    private Integer id;
    private String name;
}
