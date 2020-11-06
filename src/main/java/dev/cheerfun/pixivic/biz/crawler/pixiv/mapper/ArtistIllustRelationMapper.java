package dev.cheerfun.pixivic.biz.crawler.pixiv.mapper;

import dev.cheerfun.pixivic.common.po.Illustration;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

public interface ArtistIllustRelationMapper {

    @Insert("<script>" +
            "REPLACE  INTO " +
            "artist_illust_relation (`artist_id`, `illust_id`,`illust_type`,`create_date`)" +
            "VALUES" +
            "<foreach collection='illustrations' item='illustration' index='index'  separator=','>" +
            "(#{illustration.artistId}," +
            "#{illustration.id}," +
            "#{illustration.id}, " +
            "(case #{illustration.type} WHEN 'illust' THEN 1" +
            "WHEN 'manga' THEN 2" +
            "ELSE 3 end)," +
            "#{illustration.createDate})" +
            "</foreach>" +
            "</script>")
    void batchiInsertArtistIllustRelation(List<Illustration> illustrations);

}
