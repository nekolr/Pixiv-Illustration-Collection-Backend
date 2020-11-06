package dev.cheerfun.pixivic.biz.crawler.bangumi.mapper;

import dev.cheerfun.pixivic.biz.crawler.bangumi.domain.Animate;
import dev.cheerfun.pixivic.biz.crawler.bangumi.domain.AnimateCharacter;
import dev.cheerfun.pixivic.biz.crawler.bangumi.domain.Seiyuu;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

public interface AnimateMapper {

    @Insert("<script>" +
            "REPLACE  INTO " +
            "animates (`animate_id`, `title`, `translated_title`, `type`, `detail`, `intro`, `tags`, `cover`, `rate`, `characters`)" +
            "VALUES" +
            "<foreach collection='animateList' item='animate' index='index'  separator=','>" +
            "(#{animate.id}," +
            "#{animate.title}," +
            "#{animate.translatedTitle}," +
            "#{animate.type}," +
            "#{animate.detail,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}," +
            "#{animate.intro}," +
            "#{animate.tags,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}," +
            "#{animate.cover}," +
            "#{animate.rate}," +
            "#{animate.characters,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler})" +
            "</foreach>" +
            "</script>")
    Integer insertAnimateList(@Param("animateList") List<Animate> animateList);

    @Insert("<script>" +
            "REPLACE  INTO " +
            "animate_characters (`character_id`,`animate_id`,`seiyuu_id`, `avatar`, `character_name`, `translated_name`, `detail`, `type`, `seiyuu`)" +
            "VALUES" +
            "<foreach collection='characterList' item='character' index='index'  separator=','>" +
            "(#{character.id}," +
            "#{animateId}," +
            "#{character.seiyuu.id}," +
            "#{character.avatar}," +
            "#{character.name}," +
            "#{character.translatedName}," +
            "#{character.detail}," +
            "#{character.type}," +
            "#{character.seiyuu,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler})" +
            "</foreach>" +
            "</script>")
    Integer insertCharacterList(@Param("characterList") List<AnimateCharacter> characterList, Integer animateId);

    @Insert("<script>" +
            "REPLACE  INTO " +
            "seiyuus (`seiyuu_id`,`seiyuu_name`, `translated_name`, `avatar`)" +
            "VALUES" +
            "<foreach collection='seiyuuList' item='seiyuu' index='index'  separator=','>" +
            "(#{seiyuu.id}," +
            "#{seiyuu.name}," +
            "#{seiyuu.translatedName}," +
            "#{seiyuu.avatar})" +
            "</foreach>" +
            "</script>")
    Integer insertSeiyuuList(@Param("seiyuuList") List<Seiyuu> seiyuuList);

    @Select("select tags from animates")
    List<String> queryAllTag();

    @Insert("<script>" +
            "insert ignore  INTO " +
            "collection_tag (`tag_name`)" +
            "VALUES" +
            "<foreach collection='set' item='s' index='index'  separator=','>" +
            "(#{s})" +
            "</foreach>" +
            "</script>")
    void insertTags(@Param("set") Set<String> set);
}
