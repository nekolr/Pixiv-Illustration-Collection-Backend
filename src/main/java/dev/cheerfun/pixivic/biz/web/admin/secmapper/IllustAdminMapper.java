package dev.cheerfun.pixivic.biz.web.admin.secmapper;

import dev.cheerfun.pixivic.biz.web.admin.dto.IllustDTO;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface IllustAdminMapper {
    @Update({
            "<script>",
            "update illusts set",
            "<when test='illustDTO.xRestrict!=null'>\n",
            "x_restrict=#{illustDTO.xRestrict}\n",
            "</when>",
            "<when test='illustDTO.sanityLevel!=null'>\n",
            ",sanity_level=#{illustDTO.sanityLevel}\n",
            "</when>",
            "where illust_id=#{illustDTO.id}",
            "</script>"
    })
    void updateIllusts(IllustDTO illustDTO);

    @Select("select illust_id from illusts where artist_id = #{artistId}")
    List<Integer> queryIllustrationsByArtistId(Integer artistId);
}
