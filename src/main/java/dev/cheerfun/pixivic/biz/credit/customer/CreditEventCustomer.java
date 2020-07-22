package dev.cheerfun.pixivic.biz.credit.customer;

import dev.cheerfun.pixivic.biz.credit.mapper.CreditMapper;
import dev.cheerfun.pixivic.biz.credit.po.CreditConfig;
import dev.cheerfun.pixivic.biz.credit.po.CreditHistory;
import dev.cheerfun.pixivic.biz.event.domain.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
//@RabbitListener(queues = "creditQueue")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreditEventCustomer {

    private final CreditMapper creditMapper;
    private Map<String, CreditConfig> creditConfigMap;
    private final StringRedisTemplate stringRedisTemplate;

    //@PostConstruct
    void init() {
        //初始化配置
        try {
            creditConfigMap = creditMapper.queryCreditConfig().stream().collect(Collectors.toMap(e -> e.getObjectType() + ":" + e.getAction(), e -> e));
        } catch (Exception exception) {
            log.error("积分事件消费者初始化失败");
        }
    }

    //@RabbitHandler()
    @Transactional(rollbackFor = Exception.class)
    public void consume(Event event) {
        //取出配置
        CreditConfig creditConfig = creditConfigMap.get(event.getObjectType() + ":" + event.getAction());
        if (creditConfig != null) {
            //判断次数
            if (limitCheck(event.getUserId(), event.getObjectType(), event.getAction(), creditConfig.getLimitNum())) {
                Integer score;
                //是否随机
                if (creditConfig.getIsRandom().compareTo(1) == 0) {
                    //取出上下界限 生成随机值
                    score = creditConfig.getRandomStart() + (int) (Math.random() * (creditConfig.getRandomEnd() - creditConfig.getRandomStart() + 1));
                } else {
                    score = creditConfig.getScore();
                }
                //持久化
                //用户积分增加
                creditMapper.increaseUserScore(event.getUserId(), score);
                //积分纪录增加
                creditMapper.insertCreditLog(new CreditHistory(null, event.getUserId(), event.getObjectType(), event.getObjectId(), event.getAction(), 1, score, creditConfig.getDesc(), null));
            }
        }
        System.out.println(event);
    }

    protected boolean limitCheck(Integer userId, String objectType, String action, Integer limitNum) {
        //去记录表查询今天该类型加分的次数
        return limitNum > creditMapper.queryCreditCount(userId, objectType, action);
    }

}
