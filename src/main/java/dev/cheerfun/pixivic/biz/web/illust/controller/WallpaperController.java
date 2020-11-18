package dev.cheerfun.pixivic.biz.web.illust.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.ratelimit.annotation.RateLimit;
import dev.cheerfun.pixivic.biz.userInfo.annotation.WithUserInfo;
import dev.cheerfun.pixivic.biz.wallpaper.po.WallpaperCategory;
import dev.cheerfun.pixivic.biz.web.illust.service.WallpaperService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WallpaperController {
    private final WallpaperService wallpaperService;

    @GetMapping("/wallpaper/category")
    @PermissionRequired
    public ResponseEntity<Result<List<WallpaperCategory>>> queryAllCategory(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("获取壁纸分类成功", wallpaperService.queryAllCategory()));
    }

    @GetMapping("/wallpaper/category/{categotyId}/tags")
    @PermissionRequired
    public ResponseEntity<Result<List<Tag>>> queryTagListByCategory(@PathVariable Integer categotyId, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "30") @Max(30) int pageSize, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("分页获取分类下标签成功", wallpaperService.queryTagCountByCategory(categotyId), wallpaperService.queryTagListByCategory(categotyId, offset, pageSize)));
    }

    @GetMapping("/wallpaper/category/{categotyId}/tags/{tagId}/type/{type}/illusts")
    @WithUserInfo
    @PermissionRequired
    public ResponseEntity<Result<List<Illustration>>> queryIllustIdByTag(@PathVariable Integer categotyId, @PathVariable Integer tagId, @PathVariable Integer type, @RequestParam(defaultValue = "2147483647") int offset, @RequestParam(defaultValue = "30") @Max(30) int pageSize, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("分页获取标签下画作成功", wallpaperService.queryIllustCountByTag(tagId, type), wallpaperService.queryIllustByTag(tagId, type, offset, pageSize)));
    }

    @GetMapping("/wallpaper/{platform}/random")
    @RateLimit
    public ResponseEntity<Result<Illustration>> queryRandomIllustration(@PathVariable String platform, @RequestParam(value = "size", defaultValue = "original") String size, @RequestParam(value = "webp", defaultValue = "1") String webp, @RequestParam(value = "domain", defaultValue = "https://i.pixiv.cat") String domain, @RequestHeader("X-Forwarded-For") String XForwardedFor) {
        String url = wallpaperService.queryRandomIllustration("pc".equals(platform) ? 1 : 2, size);
        if ("0".equals(webp)) {
            url = url.replace("_webp", "");
        }
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header("Location", url.replace("https://i.pximg.net", domain)).header("Cache-Control", "no-cache").body(null);
    }

}
