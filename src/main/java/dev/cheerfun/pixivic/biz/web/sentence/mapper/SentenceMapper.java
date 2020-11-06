package dev.cheerfun.pixivic.biz.web.sentence.mapper;

import dev.cheerfun.pixivic.biz.web.sentence.po.Sentence;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface SentenceMapper {

    @Select("select * from sentences where sentence_id=#{sentenceId}")
    @Results({
            @Result(column = "sentence_id", property = "id"),
            @Result(column = "originate_from", property = "originateFrom"),
            @Result(column = "originate_from_jp", property = "originateFromJP")
    })
    Sentence querySentenceById(Integer sentenceId);

    @Select("select count(*) from sentences")
    Integer querySentenceListSize();

}
