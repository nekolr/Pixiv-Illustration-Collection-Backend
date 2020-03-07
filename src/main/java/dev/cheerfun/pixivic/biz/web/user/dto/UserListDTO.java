package dev.cheerfun.pixivic.biz.web.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/7 11:10 上午
 * @description UserList
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserListDTO {
    private String username;
    private Integer userId;
    private LocalDateTime createDate;

}
