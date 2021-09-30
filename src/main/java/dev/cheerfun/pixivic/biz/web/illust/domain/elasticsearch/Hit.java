package dev.cheerfun.pixivic.biz.web.illust.domain.elasticsearch;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/09 23:05
 * @description Hit
 */
@Data
public class Hit<T> {
    @JsonAlias({"_source", "t"})
    private T t;
}
