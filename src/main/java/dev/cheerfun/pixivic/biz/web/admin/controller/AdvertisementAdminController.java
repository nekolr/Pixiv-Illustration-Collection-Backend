package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.admin.po.AdvertisementPO;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/28 5:17 PM
 * @description AdvertisementAdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class AdvertisementAdminController {
    private final AdminService adminService;

    //广告管理
    @PostMapping("/ads")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<AdvertisementPO>>> queryAdvertisements(
            @RequestBody AdvertisementPO advertisementPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization") String token) {
        Page<AdvertisementPO> advertisementPOS = adminService.queryAdvertisements(advertisementPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取广告列表成功", advertisementPOS.getTotalElements(), advertisementPOS.getContent()));
    }

    @PutMapping("/ads/{adId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<AdvertisementPO>> updateAdvertisement(
            @PathVariable Integer adId,
            @RequestBody AdvertisementPO advertisementPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("更新广告成功", adminService.updateAdvertisement(advertisementPO)));
    }

    @PutMapping("/adList")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> updateAdvertisementList(
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("更新广告成功", adminService.updateAdvertisementList()));
    }

    @PutMapping("/ads")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<AdvertisementPO>> createAdvertisement(
            @RequestBody AdvertisementPO advertisementPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("发布广告成功", adminService.createAdvertisement(advertisementPO)));
    }

    @DeleteMapping("/ads/{adId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteAdvertisement(
            @PathVariable Integer adId,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除广告成功", adminService.deleteAdvertisement(adId)));
    }

}
