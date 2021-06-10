package dev.cheerfun.pixivic.biz.dns.service;

import com.jdcloud.sdk.JdcloudSdkException;
import com.jdcloud.sdk.service.clouddnsservice.client.ClouddnsserviceClient;
import com.jdcloud.sdk.service.clouddnsservice.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NoHttpResponseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/31 11:19 PM
 * @description JDDNSModifyService
 */
@AllArgsConstructor
@Service
@Slf4j
public class JDDNSModifyService {

    private final ClouddnsserviceClient clouddnsserviceClient;

    @Scheduled(cron = "0 30 1,22 * * ?")
    public void dailyTask() {
        log.info("开始修改dns解析");
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int dayOfWeek = now.getDayOfWeek().getValue();
        boolean flag = true;
        while (flag) {
            try {
                if (hour >= 22) {
                    if (dayOfWeek >= 1 && dayOfWeek < 5) {
                        modifyDNSLB(1);
                    } else {
                        modifyDNSLB(2);
                    }
                    return;
                }
                if (hour >= 1) {
                    if (dayOfWeek >= 1 && dayOfWeek < 5) {
                        modifyDNSLB(0);
                    } else {
                        modifyDNSLB(1);
                    }
                }
                flag = false;
            } catch (JdcloudSdkException e) {
                log.info("修改dns解析失败，即将重试");
                flag = true;
            }
        }
        log.info("修改dns解析结束");
    }

    //晚高峰前调整dns负载均衡
    public void modifyDNSLB(Integer weight) {
        SearchRRResponse searchRRResponse = clouddnsserviceClient.searchRR(new SearchRRRequest().domainId("4304").regionId("cn-north-1").pageNumber(2).pageSize(10));
        SearchRRResult result = searchRRResponse.getResult();
        List<RR> dataList = result.getDataList();
        dataList.stream().filter(e -> e.getId() == 32622660 || e.getId() == 26235035 || e.getId() == 26235034).map(e -> {
                    UpdateRR updateRR = new UpdateRR();
                    updateRR.id(e.getId()).hostRecord(e.getHostRecord()).hostValue(e.getHostValue()).type(e.getType()).viewValue(e.getViewValue().get(0)).ttl(60).weight(weight);
                    return updateRR;
                }
        ).forEach(
                e -> {
                    clouddnsserviceClient.updateRR(new UpdateRRRequest().req(e).domainId("4304").regionId("cn-north-1"));
                }
        );

    }
}
