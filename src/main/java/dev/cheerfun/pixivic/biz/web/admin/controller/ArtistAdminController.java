package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
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
 * @date 2020/10/28 5:10 PM
 * @description ArtistAdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class ArtistAdminController {
    private final AdminService adminService;

    @PostMapping("/blockArtists")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> blockArtistById(@RequestHeader(value = "Authorization") String token, @RequestBody List<Integer> artistIdList) {
        adminService.blockArtistById(artistIdList);
        return ResponseEntity.ok().body(new Result<>("封禁画师成功", true));
    }

    @GetMapping("/blockArtists")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<Integer>>> queryBlockArtist(@RequestParam(required = false) Integer artistId, @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("获取封禁画师成功", adminService.queryBlockArtist(artistId)));
    }

    @DeleteMapping("/blockArtists/{artistId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> removeArtistFromBlockArtist(@PathVariable Integer artistId, @RequestHeader(value = "Authorization") String token) {
        adminService.removeArtistFromBlockArtist(artistId);
        return ResponseEntity.ok().body(new Result<>("移除封禁画师成功", true));
    }

}
