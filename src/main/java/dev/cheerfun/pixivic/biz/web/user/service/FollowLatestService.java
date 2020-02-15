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

    @Cacheable(value = "followedLatest", key = "#userId+#type")
    public List<Integer> queryFollowedLatestSortedIllustId(int userId, String type) {
        System.out.println(new Date()+": 开始找查询id列表");
        //取出最近一个月关注画师的画作id
        List<Integer> illustrationIdList = businessMapper.queryFollowedLatestIllustId(userId, type);
        //遍历切割出k个升序数组,用大根堆进行合并得到TOP3000(最多3000,多了业务上没有意义)
        System.out.println(new Date()+": 开始切割排序");
        List<Integer> sortedIdList = mergekSortedArrays(split(illustrationIdList));
        System.out.println(new Date()+": 排序结束");
        return sortedIdList;
    }

    private static List<List<Integer>> split(List<Integer> illustrationIdList) {
        List<List<Integer>> result = new ArrayList<>();
        int size = illustrationIdList.size();
        if (size > 1) {
            int from = 0;
            int to = 1;
            for (; to < size; to++) {
                if (to == size - 1) {
                    result.add(Lists.reverse(illustrationIdList.subList(from, to + 1)));
                    break;
                } else if (to != size - 1 && illustrationIdList.get(to) > illustrationIdList.get(to + 1)) {
                    result.add(Lists.reverse(illustrationIdList.subList(from, to + 1)));
                    from = to + 1;
                }
            }
        }
        return result;
    }

    private static class NewInteger {
        int value, row, col;

        public NewInteger(int value, int row, int col) {
            this.value = value;
            this.row = row;
            this.col = col;
        }
    }

    public static List<Integer> mergekSortedArrays(List<List<Integer>> arrays) {
        ArrayList<Integer> list = new ArrayList<>();
        if (arrays == null || arrays.size() == 0 || arrays.get(0).size() == 0) {
            return list;
        }
        PriorityQueue<NewInteger> pq = new PriorityQueue<>(arrays.size(), (x, y) -> x.value > y.value ? -1 : 1);
        for (int i = 0; i < arrays.size(); i++) {
            pq.offer(new NewInteger(arrays.get(i).get(0), i, 0));
        }
        while (list.size() < 3000 && !pq.isEmpty()) {
            NewInteger min = pq.poll();
            if (min.col + 1 < arrays.get(min.row).size()) {
                pq.offer(new NewInteger(arrays.get(min.row).get(min.col + 1), min.row, min.col + 1));
            }
            list.add(min.value);
        }
        return list;
    }
}
