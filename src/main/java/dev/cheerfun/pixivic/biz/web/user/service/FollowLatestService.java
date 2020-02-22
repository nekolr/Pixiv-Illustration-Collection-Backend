package dev.cheerfun.pixivic.biz.web.user.service;

import com.google.common.collect.Lists;
import dev.cheerfun.pixivic.biz.web.user.mapper.BusinessMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/12 下午1:03
 * @description FollowLatestService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FollowLatestService {
    private final BusinessMapper businessMapper;


}
