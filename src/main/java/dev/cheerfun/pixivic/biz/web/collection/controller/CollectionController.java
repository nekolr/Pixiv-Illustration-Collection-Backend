package dev.cheerfun.pixivic.biz.web.collection.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.biz.web.collection.dto.UpdateIllustrationOrderDTO;
import dev.cheerfun.pixivic.biz.web.collection.service.CollectionService;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/29 5:47 下午
 * @description CollectionController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/collections")
@PermissionRequired
public class CollectionController {
    private final CollectionService collectionService;

    //新建画集
    @PostMapping
    public ResponseEntity<Result<List<User>>> createCollection(@RequestHeader(value = "Authorization", required = false) String token) {
        return null;
        // return ResponseEntity.ok().body(new Result<>("获取用户列表成功", adminService.queryUsersTotal(usersDTO, page, pageSize), adminService.queryUsers(usersDTO, page, pageSize)));
    }

    //修改画集元数据
    @PostMapping("/{collectionId}")
    public ResponseEntity<Result<List<User>>> updateCollection(@PathVariable Integer collectionId, @RequestHeader(value = "Authorization", required = false) String token) {
        return null;
    }

    //删除画集
    @DeleteMapping("/{collectionId}")
    public ResponseEntity<Result<List<User>>> deleteCollection(@PathVariable Integer collectionId, @RequestHeader(value = "Authorization", required = false) String token) {
        return null;
    }

    //将画作添加进画集
    @PostMapping("/{collectionId}/illustrations")
    public ResponseEntity<Result<List<User>>> addIllustrationToCollection(@PathVariable Integer collectionId, @RequestHeader(value = "Authorization", required = false) String token) {
        return null;
        // return ResponseEntity.ok().body(new Result<>("获取用户列表成功", adminService.queryUsersTotal(usersDTO, page, pageSize), adminService.queryUsers(usersDTO, page, pageSize)));
    }

    //从画集中删除画作
    @DeleteMapping("/{collectionId}/illustrations/{illustrationId}")
    public ResponseEntity<Result<List<User>>> deleteIllustrationFromCollection(@PathVariable Integer collectionId, @PathVariable Integer illustrationId, @RequestHeader(value = "Authorization", required = false) String token) {
        return null;
        // return ResponseEntity.ok().body(new Result<>("获取用户列表成功", adminService.queryUsersTotal(usersDTO, page, pageSize), adminService.queryUsers(usersDTO, page, pageSize)));
    }

    //画集画作排序
    @PutMapping("/{collectionId}/illustrations/order")
    public ResponseEntity<Result<List<User>>> updateIllustrationOrder(@PathVariable Integer collectionId, @RequestBody UpdateIllustrationOrderDTO updateIllustrationOrderDTO, @RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        collectionService.updateIllustrationOrder(collectionId, updateIllustrationOrderDTO, userId);

        return null;
        // return ResponseEntity.ok().body(new Result<>("获取用户列表成功", adminService.queryUsersTotal(usersDTO, page, pageSize), adminService.queryUsers(usersDTO, page, pageSize)));
    }

    //查询用户画集
    //搜索画集
    //最新公开画集列表

}
