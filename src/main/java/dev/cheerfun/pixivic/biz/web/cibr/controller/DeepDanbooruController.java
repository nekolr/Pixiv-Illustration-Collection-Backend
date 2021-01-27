/*
package dev.cheerfun.pixivic.biz.web.cibr.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.biz.web.cibr.service.DeepDanbooruService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Picture;
import dev.cheerfun.pixivic.common.po.Result;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.im4java.core.IM4JavaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

*/
/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/11/30 11:35 PM
 * @description DeepDanbooruController
 *//*

@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeepDanbooruController {
    private final DeepDanbooruService deepDanbooruService;

    @PostMapping("/images/tags")
    public ResponseEntity<Result<List<String>>> generateImageTagList(@RequestParam("file") MultipartFile file) throws IOException, InterruptedException, IM4JavaException {
        return ResponseEntity.ok().body(new Result<>("生成图片标签成功", deepDanbooruService.generateImageTagList(file)));
    }
}
*/
