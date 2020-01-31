package dev.cheerfun.pixivic.biz.web.illust.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.biz.web.user.service.BusinessService;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.ArtistSummary;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/28 16:10
 * @description IllustrationController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IllustrationBizController {
    private final IllustrationBizService illustrationBizService;
    private final BusinessService businessService;
    private static final String USER_ID = "userId";

    @GetMapping("/tags/{tag}/translation")
    public ResponseEntity<Result<Tag>> translationTag(@PathVariable String tag) {
        return ResponseEntity.ok().body(new Result<>("获取标签翻译成功", illustrationBizService.translationTag(tag)));
    }

    @GetMapping("/artists/{artistId}/illusts/{type}")
    //@PermissionRequired
    public ResponseEntity<Result<List<Illustration>>> queryIllustrationsByArtistId(@PathVariable Integer artistId, @PathVariable String type, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "30") int pageSize, @RequestParam(defaultValue = "5") int maxSanityLevel) {
        return ResponseEntity.ok().body(new Result<>("获取画师画作列表成功", illustrationBizService.queryIllustrationsByArtistId(artistId, type, (page - 1) * pageSize, pageSize, maxSanityLevel)));
    }

    @GetMapping("/artists/{artistId}/summary")
    //@PermissionRequired
    public ResponseEntity<Result<List<ArtistSummary>>> querySummaryByArtistId(@PathVariable Integer artistId) {
        return ResponseEntity.ok().body(new Result<>("获取画师画作汇总成功", illustrationBizService.querySummaryByArtistId(artistId)));
    }

    @GetMapping("/artists/{artistId}")
    //@PermissionRequired
    public ResponseEntity<Result<Artist>> queryArtistById(@PathVariable Integer artistId) {
        return ResponseEntity.ok().body(new Result<>("获取画师详情成功", illustrationBizService.queryArtistById(artistId)));
    }

    @GetMapping("/exists/{type}/{id}")
    //@PermissionRequired
    public ResponseEntity<Result<Boolean>> queryExistsById(@PathVariable String type, @PathVariable Integer id) {
        return ResponseEntity.ok().body(new Result<>("获取存在详情成功", illustrationBizService.queryExistsById(type, id)));
    }

    @GetMapping("/illusts/{illustId}")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<Illustration>> queryIllustrationById(@PathVariable Integer illustId, @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("获取画作详情成功", illustrationBizService.queryIllustrationById(illustId)));
    }

    @GetMapping("/illusts/{illustId}/related")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public CompletableFuture<ResponseEntity<Result<List<Illustration>>>> queryIllustrationRelated(@PathVariable Integer illustId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "30") int pageSize,@RequestHeader(value = "Authorization", required = false) String token) {
        //由于异步请求,线程切换导致取不到threadlocal,所以这里先取一下
        Integer userId = null;
        if (AppContext.get() != null) {
            userId = (int) AppContext.get().get(USER_ID);
        }
        Integer finalUserId = userId;
        return illustrationBizService.queryIllustrationRelated(illustId, page,pageSize).thenApply(r -> ResponseEntity.ok().body(new Result<>("获取关联画作成功", finalUserId == null ? r : businessService.dealIsLikedInfoForIllustList(r, finalUserId))));
    }

    @GetMapping("/illusts/random")
    public ResponseEntity<Result<Illustration>> queryRandomIllustration(
            @RequestParam(defaultValue = "original") String urlType,
            @RequestParam(defaultValue = "illust") String illustType,
            @RequestParam(defaultValue = "false") Boolean detail,
            @RequestParam(defaultValue = "1") Float range,
            @RequestParam(defaultValue = "16:9") String ratio,
            @RequestParam(defaultValue = "4") Integer maxSanityLevel
    ) {
        String url = illustrationBizService.queryRandomIllustration(urlType, illustType, detail, ratio, range, maxSanityLevel);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header("Location", url).header("Cache-Control", "no-cache").body(null);
    }

    @GetMapping("/tags/{tag}/candidates")
    public ResponseEntity<Result<List<Tag>>> autoCompleteTag(@PathVariable String tag, @RequestBody List<String> tagList) {
        return ResponseEntity.ok().body(new Result<>("获取标签候选成功", null));
    }

}
