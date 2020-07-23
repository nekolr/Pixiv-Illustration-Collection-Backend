package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.admin.dto.UsersDTO;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/24 2:46 下午
 * @description AdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final IllustrationBizService illustrationBizService;

    @GetMapping("/illusts/{illustId}")
    public ResponseEntity<Result<Illustration>> queryIllustrationById(@PathVariable Integer illustId, @RequestHeader(value = "Token", required = false) String token) {
        if (adminService.validateKey(token)) {
            log.info("管理员key:" + token + ",开始获取画作(" + illustId + ")详情");
            return ResponseEntity.ok().body(new Result<>("获取画作详情成功", illustrationBizService.queryIllustrationByIdWithUserInfo(illustId)));
        }
        return ResponseEntity.ok().body(new Result<>("获取画作详情成功", null));
    }

    @PostMapping("/users")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<User>>> queryUsers(
            @RequestBody UsersDTO usersDTO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "user_id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("获取用户列表成功", adminService.queryUsersTotal(usersDTO, page, pageSize), adminService.queryUsers(usersDTO, page, pageSize, orderBy, orderByMode)));
    }

    @PutMapping("/users")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> updateUser(@RequestBody UsersDTO usersDTO, @RequestHeader(value = "Authorization", required = false) String token) {
        adminService.updateUser(usersDTO);
        return ResponseEntity.ok().body(new Result<>("更新用户成功", true));
    }

    //@DeleteMapping("/users/{userId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> banUser(@PathVariable Integer userId, @RequestHeader(value = "Authorization", required = false) String token) {
        adminService.banUser(userId);
        return ResponseEntity.ok().body(new Result<>("封禁用户成功", true));
    }

    public ResponseEntity<Result<List<Comment>>> queryComment(
            @RequestBody Comment comment,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "comment_id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("获取评论列表成功", adminService.queryCommentTotal(comment, page, pageSize), adminService.queryComment(comment, page, pageSize, orderBy, orderByMode)));
    }

//    @PutMapping("/illusts")
//    public ResponseEntity<Result<User>> updateIllusts(@RequestBody @Valid IllustDTO illustDTO, @RequestHeader(value = "Authorization", required = false) String token) {
//        adminService.updateIllusts(illustDTO);
//        return null;
//    }

}
