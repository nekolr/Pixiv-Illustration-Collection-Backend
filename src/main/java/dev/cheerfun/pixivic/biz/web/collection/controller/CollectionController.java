package dev.cheerfun.pixivic.biz.web.collection.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.biz.web.collection.dto.UpdateIllustrationOrderDTO;
import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import dev.cheerfun.pixivic.biz.web.collection.service.CollectionService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Illustration;
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
//@RequestMapping("/collections")
public class CollectionController {
    private final CollectionService collectionService;

    //新建画集
    @PostMapping
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> createCollection(@RequestBody @SensitiveCheck Collection collection, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("新建画集成功", collectionService.createCollection(userId, collection)));
    }

    //修改画集元数据
    @PostMapping("/collections/{collectionId}")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> updateCollection(@PathVariable Integer collectionId, @RequestBody @SensitiveCheck Collection collection, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("修改画集成功", collectionService.updateCollection(userId, collection)));
    }

    //删除画集
    @DeleteMapping("/collections/{collectionId}")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> deleteCollection(@PathVariable Integer collectionId, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("删除画集成功", collectionService.deleteCollection(userId, collectionId)));
    }

    //将画作添加进画集
    @PostMapping("/collections/{collectionId}/illustrations")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> addIllustrationToCollection(@PathVariable Integer collectionId, @RequestBody Illustration illustration, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("添加画作成功", collectionService.addIllustrationToCollection(userId, collectionId, illustration)));
    }

    //从画集中删除画作
    @DeleteMapping("/collections/{collectionId}/illustrations/{illustrationId}")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> deleteIllustrationFromCollection(@PathVariable Integer collectionId, @PathVariable Integer illustrationId, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("删除画作成功", collectionService.deleteIllustrationFromCollection(userId, collectionId, illustrationId)));
    }

    //画集画作排序
    @PutMapping("/collections/{collectionId}/illustrations/order")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> updateIllustrationOrder(@PathVariable Integer collectionId, @RequestBody UpdateIllustrationOrderDTO updateIllustrationOrderDTO, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("更新排序成功", collectionService.updateIllustrationOrder(collectionId, updateIllustrationOrderDTO, userId)));
    }

    //查询用户画集
    @GetMapping("/users/{userId}/collections")
    public ResponseEntity<Result<List<Collection>>> queryUserCollection(@PathVariable Integer userId, @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("更新排序成功", collectionService.queryUserCollection(userId)));
    }

    //搜索画集
    @GetMapping("/collections")
    public ResponseEntity<Result<Boolean>> searchCollection(@RequestBody UpdateIllustrationOrderDTO updateIllustrationOrderDTO, @RequestHeader(value = "Authorization", required = false) String token) {
        return null;
    }

    //最新公开画集列表
    @GetMapping("/latestPublicCollections")
    public ResponseEntity<Result<Boolean>> queryLatestPublicCollection(@PathVariable Integer collectionId, @RequestBody UpdateIllustrationOrderDTO updateIllustrationOrderDTO, @RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return null;
    }

}
