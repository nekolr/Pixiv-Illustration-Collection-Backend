package dev.cheerfun.pixivic.biz.web.user.service;

import dev.cheerfun.pixivic.biz.credit.po.CreditHistory;
import dev.cheerfun.pixivic.biz.web.user.mapper.UserCreditMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/30 7:12 PM
 * @description UserCreditService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserCreditService {
    private final UserCreditMapper userCreditMapper;

    @Cacheable("userRecentlyCreditHistoryList")
    public List<Integer> queryRecentlyCreditHistoryList(int userId) {
        return userCreditMapper.queryRecentlyCreditHistoryList(userId);
    }

    @Cacheable("userCreditHistory")
    public CreditHistory queryCreditHistoryById(Integer creditHistoryId) {
        return userCreditMapper.queryCreditHistoryById(creditHistoryId);
    }

    public Integer queryCreditHistoryCount(int userId) {
        return queryRecentlyCreditHistoryList(userId).size();
    }

    public List<CreditHistory> queryCreditHistoryList(int userId, Integer page, Integer pageSize) {
        return queryRecentlyCreditHistoryList(userId).stream().skip(pageSize * (page - 1))
                .limit(pageSize).map(this::queryCreditHistoryById).collect(Collectors.toList());
    }
}
