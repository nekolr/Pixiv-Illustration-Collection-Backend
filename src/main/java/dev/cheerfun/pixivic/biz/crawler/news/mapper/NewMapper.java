package dev.cheerfun.pixivic.biz.crawler.news.mapper;

import dev.cheerfun.pixivic.common.po.ACGNew;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NewMapper {

    @Select({
            "<script>",
            "SELECT temp.title FROM (",
            "<foreach collection='titleList' item='t' index='index' separator='UNION ALL'>",
            "(SELECT #{t} AS title )",
            "</foreach>",
            ") as temp WHERE temp.title  NOT IN (SELECT news.title FROM news)",
            "</script>"
    })
    List<String> queryNewsNotInDb(@Param("titleList") List<String> titleList);

    @Insert({
            "<script>",
            "insert IGNORE into news (`title`, `intro`, `author`,`cover`, `referer_url`, `content`, `referer`, `create_date`) VALUES",
            "<foreach collection='acgNewList' item='acgNew' index='index' separator=','>",
            "(#{acgNew.title}," +
                    "#{acgNew.intro}, #{acgNew.author}," +
                    "#{acgNew.cover}, #{acgNew.refererUrl}," +
                    "#{acgNew.content},#{acgNew.referer}, #{acgNew.createDate}" +
                    ")",
            "</foreach>",
            "</script>"
    })
    int insert(@Param("acgNewList") List<ACGNew> acgNewList);
}
