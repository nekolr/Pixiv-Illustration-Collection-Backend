package dev.cheerfun.pixivic.biz.recommend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.recommend.secmapper.RecommendInitMapper;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/8/5 10:24 PM
 * @description RecommendInitService
 */
@Service
@Slf4j
public class RecommendInitService {
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    @Autowired
    protected RecommendInitMapper recommendInitMapper;
    @Autowired
    protected IllustrationBizService illustrationBizService;
    @Autowired
    protected ObjectMapper objectMapper;

    // @PostConstruct
    public void newRecommendDataInit() {
        //取ID index
        Integer id = Integer.valueOf(Optional.ofNullable(stringRedisTemplate.opsForValue().get("r:l:id:")).orElse("434721"));
        //抽取illust,每万个一批次,记录末尾元素id到redis
        List<Illustration> illsuIdList = recommendInitMapper.queryIllustToInertItem(id);
        while (illsuIdList.size() != 0) {
            illsuIdList.stream().parallel().forEach(e -> {

                Illustration illustration = objectMapper.convertValue(e, new TypeReference<Illustration>() {
                });
                System.out.println("正在插入：" + e.getId());
                recommendInitMapper.insertIterm(illustration.getId(), illustration.getTags() == null ? new ArrayList<>() : illustration.getTags().stream().map(t -> String.valueOf(t.getId())).collect(Collectors.toList()), e.getCreateDate());
                //stringRedisTemplate.opsForValue().set("r:l:id:", String.valueOf(e.getId()));
            });
            stringRedisTemplate.opsForValue().set("r:l:id:", String.valueOf(illsuIdList.get(illsuIdList.size() - 1).getId()));
            illsuIdList = recommendInitMapper.queryIllustToInertItem(illsuIdList.get(illsuIdList.size() - 1).getId());
        }

        //插入到item表中 （insert ignore）
        //取bookmark表 限制类型为illust 每万个一批次
        //插入feedback表
        //从feedbak插入user表（无label）

    }
}
