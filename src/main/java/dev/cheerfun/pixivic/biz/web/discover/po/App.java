package dev.cheerfun.pixivic.biz.web.discover.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/24 下午9:31
 * @description App
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class App extends DiscoverNode {

}
