package dev.cheerfun.pixivic.biz.web.discover.controller;

import dev.cheerfun.pixivic.biz.web.discover.po.DiscoverNode;
import dev.cheerfun.pixivic.biz.web.discover.service.DiscoverService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/24 下午9:42
 * @description DiscoverCOntroller
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DiscoverController {
    private final DiscoverService discoverService;

    @GetMapping("/discoverNodes")
    public ResponseEntity<Result<List<DiscoverNode>>> queryAllNodes() {
        return ResponseEntity.ok().body(new Result<>("拉取发现节点成功", discoverService.queryAllNodes()));
    }
}
