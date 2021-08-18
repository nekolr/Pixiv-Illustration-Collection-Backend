package dev.cheerfun.pixivic.biz.recommend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.recommend.domain.UREvent;
import dev.cheerfun.pixivic.biz.recommend.domain.URRec;
import dev.cheerfun.pixivic.biz.recommend.mapper.RecommendMapper;
import dev.cheerfun.pixivic.biz.recommend.secmapper.RecommendInitMapper;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/8/5 10:24 PM
 * @description RecommendInitService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecommendSyncService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final RecommendMapper recommendMapper;
    private final ExecutorService recommendExecutorService;

    @Value("${harness.url}")
    private String harnessUrl;

    @PostConstruct
    public void init() {
        log.info("开始初始化推荐服务");
        initTask();
        log.info("初始化推荐服务成功");

    }

    public void initTask() {
        recommendExecutorService.submit(() -> {
            while (true) {
                try {
                    syncBookmarkToHarness();
                    Thread.sleep(60 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //每分钟从收藏表中取数据取数据，进行插入到harness，更新redis的indexId
    public void syncBookmarkToHarness() {
        Boolean flag = true;
        Integer bookMarkIdIndex = Integer.valueOf(stringRedisTemplate.opsForValue().get(RedisKeyConstant.SYNC_BOOKMARK_TO_HARNESS_INDEX));
        while (flag) {
            List<UREvent> urEvents = recommendMapper.queryBookmarkById(bookMarkIdIndex);
            if (urEvents.size() == 0) {
                flag = false;
            }
            urEvents.forEach(e -> {
                e.setEvent("bookmark");
                e.setEntityType("user");
                e.setTargetEntityType("illust");
                //发送到
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(harnessUrl + "/engines/pixivic/queries")).POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(e))).build();
                    String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
                } catch (Exception ex) {
                    log.error("同步到harness出错" + e);
                    ex.printStackTrace();
                    return;
                }
            });
            stringRedisTemplate.opsForValue().set(RedisKeyConstant.SYNC_BOOKMARK_TO_HARNESS_INDEX, urEvents.get(urEvents.size() - 1).getEventId());
        }
    }

    public List<URRec> queryRecommendByUser(int userId, int num) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(harnessUrl + "/engines/pixivic/events")).POST(HttpRequest.BodyPublishers.ofString("{\"user\":\"" + userId + "\",\"num\":" + num + "}")).build();
            String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return objectMapper.readValue(body, new TypeReference<List<URRec>>() {
            });
        } catch (Exception ex) {
            log.error("从harness拉取推荐出错" + ex);
            ex.printStackTrace();
        }
        return null;
    }

    @Cacheable("queryRecommendByPop")
    public List<URRec> queryRecommendByPop() {
        return queryRecommendByUser(0, 900);
    }

    //异步删除
    @Async("recommendExecutorService")
    public void syncUnBookmarkToHarness(int userId, int illustId) {

    }

    /*    public void newRecommendDataInit() throws IOException {
     *//*        //取ID index
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
        //从feedbak插入user表（无label）*//*

        //取出所有feedback转为json 存入文件
        List<UREvent> urEvents = recommendInitMapper.queryAllFeedback();
        List<String> lines = urEvents.stream().map(e -> {
            e.setEvent("bookmark");
            e.setEntityType("user");
            e.setTargetEntityType("illust");
            try {
                return objectMapper.writeValueAsString(e);
            } catch (JsonProcessingException jsonProcessingException) {
                return "";
            }
        }).collect(Collectors.toList());
        Path file = Paths.get("/Users/oysterqaq/Desktop/import.txt");
        Files.write(file, lines, StandardCharsets.UTF_8);

    }*/
}
