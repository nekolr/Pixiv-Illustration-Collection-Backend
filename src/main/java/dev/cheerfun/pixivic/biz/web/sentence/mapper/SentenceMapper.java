package dev.cheerfun.pixivic.biz.web.sentence.mapper;

import dev.cheerfun.pixivic.biz.web.sentence.po.Sentence;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SentenceMapper {
    @Insert("insert ignore into sentences (content,`from`,from_who) values(#{content},#{from},#{fromWho})")
    void insertSentence(Sentence e);

}
