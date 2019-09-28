package dev.cheerfun.pixivic.web.illust.controller;

import dev.cheerfun.pixivic.common.model.Artist;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.Result;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import dev.cheerfun.pixivic.web.illust.service.IllustrationBizService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/tags/{tag}/translation")
    public ResponseEntity<Result<Tag>> translationTag(@PathVariable String tag, @RequestHeader("Authorization") String token, @RequestBody List<String> tagList) {
        return ResponseEntity.ok().body(new Result<>("获取标签翻译成功", illustrationBizService.translationTag(tag)));
    }

    @GetMapping("/artists/{artistId}/illusts")
    public ResponseEntity<Result<List<Illustration>>> queryIllustrationsByArtistId(@PathVariable String artistId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("获取画师画作列表成功", illustrationBizService.queryIllustrationsByArtistId(artistId, (page - 1) * pageSize, pageSize)));
    }

    @GetMapping("/artists/{artistId}")
    public ResponseEntity<Result<Artist>> queryArtistById(@PathVariable String artistId, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("获取画师画作列表成功", illustrationBizService.queryArtistById(artistId)));
    }

    @GetMapping("/illusts/{illustId}")
    public ResponseEntity<Result<Illustration>> queryIllustrationById(@PathVariable String illustId, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("获取画作详情成功", illustrationBizService.queryIllustrationById(illustId)));
    }

    @GetMapping("/tags/{tag}/candidates")
    public ResponseEntity<Result<List<Tag>>> autoCompleteTag(@PathVariable String tag, @RequestHeader("Authorization") String token, @RequestBody List<String> tagList) {
        return ResponseEntity.ok().body(new Result<>("获取标签候选成功", null));
    }

}
