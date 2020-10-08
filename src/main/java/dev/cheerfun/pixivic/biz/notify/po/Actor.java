package dev.cheerfun.pixivic.biz.notify.po;

import dev.cheerfun.pixivic.biz.web.common.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/8 8:58 PM
 * @description Actor
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Actor {
    private Integer userId;
    private String username;

    public static Actor castFromUser(User user) {
        return new Actor(user.getId(), user.getUsername());
    }

}
