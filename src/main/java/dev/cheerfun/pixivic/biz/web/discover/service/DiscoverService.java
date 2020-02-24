package dev.cheerfun.pixivic.biz.web.discover.service;

import dev.cheerfun.pixivic.biz.web.discover.mapper.DiscoverMapper;
import dev.cheerfun.pixivic.biz.web.discover.po.Dir;
import dev.cheerfun.pixivic.biz.web.discover.po.DiscoverNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/24 下午9:43
 * @description DiscoverService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DiscoverService {
    private final DiscoverMapper discoverMapper;

    @Cacheable("discoverNodes")
    public List<DiscoverNode> queryAllNodes() {
        List<DiscoverNode> discoverNodes = discoverMapper.queryAllNode();
        Map<Integer, List<DiscoverNode>> mapByPid = discoverNodes.stream().collect(Collectors.groupingBy(DiscoverNode::getPid));
        //取出顶级
        List<DiscoverNode> topDiscoverNodes = mapByPid.get(0);
        List<DiscoverNode> result = topDiscoverNodes.stream().map(e -> {
            List<DiscoverNode> subDiscoverNodes = mapByPid.get(e.getId());
            if (subDiscoverNodes != null) {
                return new Dir(e, subDiscoverNodes);
            }
            return e;
        }).collect(Collectors.toList());
        return result;

    }
}
