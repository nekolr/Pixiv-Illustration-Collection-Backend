package dev.cheerfun.pixivic.biz.cibr.controller;

import dev.cheerfun.pixivic.biz.cibr.service.DeepDanbooruService;
import dev.cheerfun.pixivic.biz.cibr.service.ReverseImageSearchService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.im4java.core.IM4JavaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

//@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CBIRController {
    private final DeepDanbooruService deepDanbooruService;
    @Value("${cbir.imageTempPath}")
    private String imageTempPath;
    private final ReverseImageSearchService reverseImageSearchService;

    @GetMapping("/images/tags")
    public ResponseEntity<Result<List<String>>> generateImageTagList(@RequestParam("imageUrl") String imageUrl) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("生成图片标签成功", deepDanbooruService.generateImageTagList(imageUrl)));
    }

    @PostMapping("/images")
    public ResponseEntity<Result<String>> generateImageTagList(@RequestParam("image") MultipartFile imageFile) throws IOException {
        UUID uuid = UUID.randomUUID();
        String suffix = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
        String filePath = imageTempPath + "/" + uuid + suffix;
        File desFile = new File(filePath);
        imageFile.transferTo(desFile);
        return ResponseEntity.ok().body(new Result<>("上传图片成功", uuid + suffix));
    }

    @PostMapping("/similarImages")
    public ResponseEntity<Result<List<Integer>>> generateSimilarImageTagList(@RequestParam("image") MultipartFile image) throws IOException, InterruptedException, IM4JavaException {
        return ResponseEntity.ok().body(new Result<>("获取相似图片成功", reverseImageSearchService.searchTopKIllustId(image)));
    }

    @GetMapping("/similarImages")
    public ResponseEntity<Result<List<Integer>>> generateSimilarImageTagList(@RequestParam("imageUrl") String imageUrl) throws IOException, InterruptedException, IM4JavaException {
        return ResponseEntity.ok().body(new Result<>("获取相似图片成功", reverseImageSearchService.searchTopKIllustId(imageUrl)));
    }

    @PostMapping("/syncIllustsInfo")
    public ResponseEntity<Result<Boolean>> generateSimilarImageTagList(@RequestBody List<Illustration> illustrationList) {
        return ResponseEntity.ok().body(new Result<>("每日同步成功", reverseImageSearchService.dailySync(illustrationList)));
    }

}

