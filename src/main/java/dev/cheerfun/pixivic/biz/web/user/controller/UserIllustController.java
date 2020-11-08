package dev.cheerfun.pixivic.biz.web.user.controller;

import dev.cheerfun.pixivic.biz.web.user.dto.UserListDTO;
import dev.cheerfun.pixivic.biz.web.user.service.BusinessService;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/11/7 9:44 PM
 * @description UserIllustController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserIllustController {
    private final BusinessService businessService;

    @GetMapping("/illusts/{illustId}/bookmarkedUsers")
    public ResponseEntity<Result<List<UserListDTO>>> queryUserListBookmarkedIllust(
            @PathVariable Integer illustId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize
    ) {
        List<UserListDTO> userList = businessService.queryUserListBookmarkedIllust(illustId, page, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取收藏该画作的用户列表成功", userList));
    }
}
