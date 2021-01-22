package dev.cheerfun.pixivic.biz.web.artist.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.ratelimit.annotation.RateLimit;
import dev.cheerfun.pixivic.biz.userInfo.annotation.WithUserInfo;
import dev.cheerfun.pixivic.biz.web.artist.service.ArtistBizService;
import dev.cheerfun.pixivic.biz.web.user.dto.UserListDTO;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.ArtistSummary;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/19 10:45 上午
 * @description ArtistBIZController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArtistBizController {
    private final ArtistBizService artistBizService;

    @GetMapping("/artists/{artistId}")
    @RateLimit
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<Artist>> queryArtistById(@PathVariable Integer artistId, @RequestHeader(value = AuthConstant.AUTHORIZATION, required = false) String token) throws InterruptedException {
        return ResponseEntity.ok().body(new Result<>("获取画师详情成功", artistBizService.queryArtistDetail(artistId)));
    }

    @GetMapping("/artists/{artistId}/followedUsers")
    public ResponseEntity<Result<List<UserListDTO>>> queryUserListFollowedArtist(
            @PathVariable Integer artistId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize
    ) {
        List<UserListDTO> userList = artistBizService.queryUserListFollowedArtist(artistId, page, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取关注该画师的用户列表成功", userList));
    }

    @GetMapping("/artists/{artistId}/illusts/{type}")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    @WithUserInfo
    @RateLimit
    public ResponseEntity<Result<List<Illustration>>> queryIllustrationsByArtistId(@PathVariable Integer artistId, @PathVariable String type, @RequestParam(defaultValue = "1") @Max(333) int page, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader(value = AuthConstant.AUTHORIZATION, required = false) String token) throws InterruptedException {
        List<Illustration> illustrationList = artistBizService.queryIllustrationsByArtistId(artistId, type, (page - 1) * pageSize, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取画师画作列表成功", null));
    }

    @GetMapping("/artists/{artistId}/summary")
    public ResponseEntity<Result<ArtistSummary>> querySummaryByArtistId(@PathVariable Integer artistId) {
        return ResponseEntity.ok().body(new Result<>("获取画师画作汇总成功", artistBizService.querySummaryByArtistId(artistId)));
    }

    @GetMapping("/artists")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    @WithUserInfo
    public CompletableFuture<ResponseEntity<Result<List<Artist>>>> queryArtistByName(@RequestParam String artistName, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize, @RequestHeader(value = AuthConstant.AUTHORIZATION, required = false) String token) {
        return artistBizService.queryArtistByName(artistName, page, pageSize).thenApply(e -> ResponseEntity.ok().body(new Result<>("获取画师搜索结果成功", e)));
    }

    @GetMapping("/artists/illustsPull/{artistId}")
    public ResponseEntity<Result<Boolean>> queryArtistById(@PathVariable Integer artistId) {
        return ResponseEntity.ok().body(new Result<>("爬取画师所有画作成功", artistBizService.pullArtistAllIllust(artistId)));
    }

    @GetMapping("/exists/artist/{id}")
    //@PermissionRequired
    public ResponseEntity<Result<Boolean>> queryExistsById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(new Result<>("获取存在详情成功", artistBizService.queryExistsById(id)));
    }
}
