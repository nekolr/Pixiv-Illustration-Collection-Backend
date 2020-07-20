package dev.cheerfun.pixivic.biz.credit.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/18 2:34 下午
 * @description NotifyEventCustomerManager
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreditEventCustomerManager {
    private final Map<String, CreditEventCustomer> notifyEventCustomerMap;

    public CreditEventCustomer getNotifyEventCustomer(String notifyEventType) {
        return notifyEventCustomerMap.get(notifyEventType);
    }
}
