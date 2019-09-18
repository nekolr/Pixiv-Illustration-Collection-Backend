package dev.cheerfun.pixivic.web.user.controller;

import dev.cheerfun.pixivic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/14 12:44
 * @description IllustrationController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/illustrations")
public class IllustrationController {
    @GetMapping("/{illustId}")
    public ResponseEntity<Result<Illustration>> queryActionHistory(@PathVariable String illustId) {
        //获取画作详情（mysql）
        return ResponseEntity.ok().body(new Result<>("获取画作详情成功", null));
    }

    @PostMapping("/{illustId}/tags")
    @PermissionRequired
    public ResponseEntity<Result<Illustration>> addTag(@PathVariable String illustId, @RequestHeader("Authorization") String token, @RequestBody List<String> tagList) {
        //更新画作tag
        return ResponseEntity.ok().body(new Result<>("获取画作详情成功", null));
    }


}
