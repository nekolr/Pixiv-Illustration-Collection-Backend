package dev.cheerfun.pixivic.biz.web.discussion.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.ratelimit.annotation.RateLimit;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.biz.web.discussion.po.Discussion;
import dev.cheerfun.pixivic.biz.web.discussion.po.Section;
import dev.cheerfun.pixivic.biz.web.discussion.service.DiscussionService;
import dev.cheerfun.pixivic.biz.web.discussion.vo.DiscussionVO;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/28 3:25 下午
 * @description DiscussionController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DiscussionController {
    private final DiscussionService discussionService;

    @PostMapping("/discussions/{sectionId}")
    @PermissionRequired
    @RateLimit
    public ResponseEntity<Result<Boolean>> createDiscussion(@PathVariable Integer sectionId, @RequestBody @SensitiveCheck Discussion discussion, @RequestHeader("Authorization") String token) {
        int userId = (int) AppContext.get().get(AuthConstant.USER_ID);
        Boolean result = discussionService.createDiscussion(discussion, userId);
        return ResponseEntity.ok().body(new Result<>("创建讨论" + (result ? "成功" : "失败"), result));
    }

    @PutMapping("/discussions/{sectionId}/{discussionId}")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> updateDiscussion(@PathVariable Integer sectionId, @PathVariable Integer discussionId, @RequestBody @SensitiveCheck Discussion discussion, @RequestHeader("Authorization") String token) {
        int userId = (int) AppContext.get().get(AuthConstant.USER_ID);
        Boolean result = discussionService.updateDiscussion(userId, discussion);
        return ResponseEntity.ok().body(new Result<>("更新讨论" + (result ? "成功" : "失败"), result));
    }

    @DeleteMapping("/discussions/{sectionId}/{discussionId}")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> deleteDiscussion(@PathVariable Integer sectionId, @PathVariable Integer discussionId, @RequestHeader("Authorization") String token) {
        int userId = (int) AppContext.get().get(AuthConstant.USER_ID);
        Boolean result = discussionService.deleteDiscussion(userId, discussionId);
        return ResponseEntity.ok().body(new Result<>("删除讨论" + (result ? "成功" : "失败"), result));
    }

    @PostMapping("/discussions/{sectionId}/{discussionId}/upOrDown")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> upOrDown(@PathVariable Integer sectionId, @PathVariable Integer discussionId, @RequestParam Integer option, @RequestHeader("Authorization") String token) {
        int userId = (int) AppContext.get().get(AuthConstant.USER_ID);
        Boolean result = discussionService.upOrDown(userId, discussionId, option);
        return ResponseEntity.ok().body(new Result<>("操作" + (result ? "成功" : "失败"), result));
    }

    @GetMapping("/discussions")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<List<DiscussionVO>>> queryList(@RequestParam Integer sectionId, @RequestParam Integer page, @RequestParam Integer pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        List<DiscussionVO> discussionList = discussionService.queryList(sectionId, page, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取讨论列表成功", discussionService.queryListCount(sectionId), discussionList));
    }

    @GetMapping("/discussions/{discussionId}")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<Discussion>> queryById(@PathVariable Integer discussionId, @RequestHeader(value = "Authorization", required = false) String token) {
        Discussion discussion = discussionService.queryById(discussionId);
        return ResponseEntity.ok().body(new Result<>("获取讨论详情成功", discussion));
    }

    @GetMapping("/sections")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<List<Section>>> querySectionList() {
        List<Section> sectionList = discussionService.querySectionList();
        return ResponseEntity.ok().body(new Result<>("获取板块列表成功", sectionList));
    }

}
