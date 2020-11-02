package dev.cheerfun.pixivic.biz.web.collection.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.biz.ad.annotation.WithAdvertisement;
import dev.cheerfun.pixivic.biz.userInfo.annotation.WithUserInfo;
import dev.cheerfun.pixivic.biz.web.collection.dto.CollectionDigest;
import dev.cheerfun.pixivic.biz.web.collection.dto.UpdateIllustrationOrderDTO;
import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import dev.cheerfun.pixivic.biz.web.collection.po.CollectionTag;
import dev.cheerfun.pixivic.biz.web.collection.service.CollectionService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import dev.cheerfun.pixivic.common.po.illust.ImageUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/29 5:47 下午
 * @description CollectionController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CollectionController {
    private final CollectionService collectionService;

    //新建画集
    @PostMapping("/collections")
    @PermissionRequired
    public ResponseEntity<Result<Integer>> createCollection(@RequestBody @SensitiveCheck Collection collection, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("新建画集成功", collectionService.createCollection(userId, collection)));
    }

    //修改画集元数据
    @PutMapping("/collections/{collectionId}")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> updateCollection(@PathVariable Integer collectionId, @RequestBody @SensitiveCheck Collection collection, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("修改画集成功", collectionService.updateCollection(userId, collection)));
    }

    //修改画集元数据
    @GetMapping("/collections/{collectionId}")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<Collection>> getCollection(@PathVariable Integer collectionId, @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("查看画集成功", collectionService.getCollection(collectionId)));
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
    public ResponseEntity<Result<List<Integer>>> addIllustrationToCollection(@PathVariable Integer collectionId, @RequestBody List<Integer> illustrationIds, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        List<Integer> illustIdList = collectionService.addIllustrationToCollection(userId, collectionId, illustrationIds);
        return ResponseEntity.ok().body(new Result<>("添加画作成功" + (illustIdList.size() > 0 ? "重复作品如下" : ""), illustIdList));
    }

    //从画集中删除画作
    @DeleteMapping("/collections/{collectionId}/illustrations/{illustrationId}")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> deleteIllustrationFromCollection(@PathVariable Integer collectionId, @PathVariable Integer illustrationId, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("删除画作成功", collectionService.deleteIllustrationFromCollection(userId, collectionId, illustrationId)));
    }

    //从画集中删除画作
    @DeleteMapping("/collections/{collectionId}/illustrations")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> deleteIllustrationFromCollection(@PathVariable Integer collectionId, @RequestBody List<Integer> illustrationIdList, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("批量删除画作成功", collectionService.deleteIllustrationFromCollection(userId, collectionId, illustrationIdList)));
    }

    //更改画集封面
    @PutMapping("/collections/{collectionId}/cover")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> updateCollectionCover(@PathVariable Integer collectionId, @RequestBody List<Integer> illustIdList, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("更新封面成功", collectionService.updateCollectionCover(userId, collectionId, illustIdList)));
    }

    //画集画作排序
    @PutMapping("/collections/{collectionId}/illustrations/order")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> updateIllustrationOrder(@PathVariable Integer collectionId, @RequestBody List<Integer> illustIdList, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("更新排序成功", collectionService.updateIllustrationOrder(collectionId, illustIdList, userId)));
    }

    //画集画作拖动排序
    @PutMapping("/collections/{collectionId}/illustrations/orderByDrag")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> updateIllustrationOrderByDrag(@PathVariable Integer collectionId, @RequestBody UpdateIllustrationOrderDTO updateIllustrationOrderDTO, @RequestHeader(value = "Authorization") String token) {
        Integer userId = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("更新排序成功", collectionService.updateIllustrationOrder(collectionId, updateIllustrationOrderDTO, userId)));
    }

    //查询用户画集
    @GetMapping("/users/{userId}/collections")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<List<Collection>>> queryUserCollection(@PathVariable Integer userId, @RequestHeader(value = "Authorization", required = false) String token, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") @Max(15) Integer pageSize, @RequestParam(required = false) Integer isPublic, @RequestParam(value = "orderBy", defaultValue = "create_time") String orderBy, @RequestParam(value = "orderByMode", defaultValue = "desc") String orderByMode) {
        Integer isSelf = collectionService.checkUserAuth(isPublic, userId);
        if ("updateTime".equals(orderBy)) {
            orderBy = "update_time";
        }
        return ResponseEntity.ok().body(new Result<>("获取用户画集成功", collectionService.queryCollectionSummary(userId, isPublic), collectionService.queryUserCollection(userId, isSelf, isPublic, page, pageSize, orderBy, orderByMode)));
    }

    //查询用户所有画集名字
    @GetMapping("/users/{userId}/collectionsDigest")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<List<CollectionDigest>>> queryUserCollectionNameList(@PathVariable Integer userId, @RequestHeader(value = "Authorization", required = false) String token, @RequestParam(required = false) Integer isPublic) {
        return ResponseEntity.ok().body(new Result<>("获取用户画集名称列表成功", collectionService.queryUserCollectionNameList((Integer) AppContext.get().get(AuthConstant.USER_ID), isPublic)));
    }

    //查看画集详情
    @GetMapping("/collections/{collectionId}/illustrations")
    @WithUserInfo
    @PermissionRequired
    //@WithAdvertisement
    public ResponseEntity<Result<List<Illustration>>> queryCollectionIllust(@PathVariable Integer collectionId, @RequestHeader(value = "Authorization", required = false) String token, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "30") @Max(30) Integer pageSize, @RequestParam(required = false) String userFinger) {
        //用户指纹 放到hyperlog记录浏览量
        if (page == 1 && userFinger != null) {
            collectionService.modifyCollectionTotalPeopleSeen(collectionId, userFinger);
        }
        return ResponseEntity.ok().body(new Result<>("获取画集下画作成功", collectionService.queryCollectionIllust(collectionId, page, pageSize)));
    }

    //最新公开画集列表
    @GetMapping("/collections/latest")
    public ResponseEntity<Result<List<Collection>>> queryLatestPublicCollection(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "30") @Max(30) Integer pageSize) {
        return ResponseEntity.ok().body(new Result<>("获取最新公开画集成功", collectionService.queryPublicCollectionCount(), collectionService.queryLatestPublicCollection(page, pageSize)));
    }

    //最热门画集列表
    @GetMapping("/collections/pop")
    public ResponseEntity<Result<List<Collection>>> querypopPublicCollection(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "30") @Max(30) Integer pageSize) {
        return ResponseEntity.ok().body(new Result<>("获取最热门公开画集成功", collectionService.queryPublicCollectionCount(), collectionService.queryPopPublicCollection(page, pageSize)));

    }

    //标签自动补全
    @GetMapping("/collections/tags")
    public CompletableFuture<ResponseEntity<Result<List<CollectionTag>>>> autocompleteCollectionTag(@RequestParam String keyword) {
        return collectionService.autocompleteCollectionTag(keyword).thenApply(e -> ResponseEntity.ok().body(new Result<>("标签补全成功", e)));
    }

    //搜索画集
    @GetMapping("/collections")
    public ResponseEntity<Result<List<Collection>>> searchCollection(@RequestBody UpdateIllustrationOrderDTO updateIllustrationOrderDTO, @RequestHeader(value = "Authorization", required = false) String token, @RequestParam("mode") String mode) {
        return null;
    }

}
