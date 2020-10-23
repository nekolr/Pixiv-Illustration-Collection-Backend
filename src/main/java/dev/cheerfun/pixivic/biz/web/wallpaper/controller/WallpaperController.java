package dev.cheerfun.pixivic.biz.web.wallpaper.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.biz.userInfo.annotation.WithUserInfo;
import dev.cheerfun.pixivic.biz.wallpaper.po.WallpaperCategory;
import dev.cheerfun.pixivic.biz.web.wallpaper.service.WallpaperService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/23 8:22 PM
 * @description WallpaperController
 */
@RestController
@PermissionRequired
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WallpaperController {
    private final WallpaperService wallpaperService;

    @GetMapping("/wallpaper/category")
    public ResponseEntity<Result<List<WallpaperCategory>>> queryAllCategory(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("获取壁纸分类成功", wallpaperService.queryAllCategory()));
    }

    @GetMapping("/wallpaper/category/{categotyId}/tags")
    public ResponseEntity<Result<List<Tag>>> queryTagListByCategory(@PathVariable Integer categotyId, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "30") @Max(30) int pageSize, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("分页获取分类下标签成功", wallpaperService.queryTagCountByCategory(categotyId), wallpaperService.queryTagListByCategory(categotyId, offset, pageSize)));
    }

    @GetMapping("/wallpaper/category/{categotyId}/tags/{tagId}/type/{type}/illusts")
    @WithUserInfo
    public ResponseEntity<Result<List<Illustration>>> queryIllustIdByTag(@PathVariable Integer categotyId, @PathVariable Integer tagId, @PathVariable Integer type, @RequestParam(defaultValue = "2147483647") int offset, @RequestParam(defaultValue = "30") @Max(30) int pageSize, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("分页获取标签下画作成功", wallpaperService.queryIllustCountByTag(tagId, type), wallpaperService.queryIllustByTag(tagId, type, offset, pageSize)));
    }

}
