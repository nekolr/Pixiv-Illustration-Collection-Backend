package dev.cheerfun.pixivic.biz.dns.service;

import com.jdcloud.sdk.service.clouddnsservice.client.ClouddnsserviceClient;
import com.jdcloud.sdk.service.clouddnsservice.model.*;
import lombok.AllArgsConstructor;
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
public class JDDNSModifyService {

    private final ClouddnsserviceClient clouddnsserviceClient;

    @Scheduled(cron = "0 0 10,3 * * ?")
    public void dailyTask() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int dayOfWeek = now.getDayOfWeek().getValue();
        if (hour == 10) {
            if (dayOfWeek >= 1 && dayOfWeek < 5) {
                modifyDNSLB(1);
            } else {
                modifyDNSLB(2);
            }
        }
        if (hour == 3) {
            if (dayOfWeek >= 1 && dayOfWeek < 5) {
                modifyDNSLB(0);
            } else {
                modifyDNSLB(1);
            }
        }
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
