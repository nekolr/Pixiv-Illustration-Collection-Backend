package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.admin.dto.IllustDTO;
import dev.cheerfun.pixivic.biz.web.admin.dto.UsersDTO;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
@PermissionRequired(PermissionLevel.ADMIN)
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/users")
    public ResponseEntity<Result<List<User>>> queryUsers(@RequestBody UsersDTO usersDTO, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "30") Integer pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("获取用户列表成功", adminService.queryUsersTotal(usersDTO, page, pageSize), adminService.queryUsers(usersDTO, page, pageSize)));
    }

    @PutMapping("/users")
    public ResponseEntity<Result<User>> updateUser(@RequestBody UsersDTO usersDTO, @RequestHeader(value = "Authorization", required = false) String token) {
        adminService.updateUser(usersDTO);
        return null;
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Result<User>> deleteUser(@PathVariable Integer userId, @RequestHeader(value = "Authorization", required = false) String token) {
        //adminService.deleteUser(userId);
        return null;
    }

    @PutMapping("/illusts")
    public ResponseEntity<Result<User>> updateIllusts(@RequestBody @Valid IllustDTO illustDTO, @RequestHeader(value = "Authorization", required = false) String token) {
        adminService.updateIllusts(illustDTO);
        return null;
    }

}
