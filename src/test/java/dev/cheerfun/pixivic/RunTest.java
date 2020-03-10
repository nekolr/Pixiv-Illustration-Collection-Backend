package dev.cheerfun.pixivic;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-06-28 23:05
 * @description
 */
public class RunTest {
    //构造Interger数
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Integer age = 1;

        CompletableFuture<Boolean> maturityFuture = CompletableFuture.supplyAsync(() -> {
            if (age < 0) {
                throw new IllegalArgumentException("Age can not be negative");
            }
            if (age > 18) {
                return true;
            } else {
                return true;
            }
        }, Executors.newFixedThreadPool(5)).handle((res, ex) -> {
            System.out.println("处理异常");
            if (ex != null) {
                System.out.println("Oops! We have an exception - " + ex.getMessage());
                return false;
            }
            return true;
        });

        System.out.println("Maturity : " + maturityFuture.get());
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
        while (list.size() < 100 && !pq.isEmpty()) {
            NewInteger min = pq.poll();
            if (min.col + 1 < arrays.get(min.row).size()) {
                pq.offer(new NewInteger(arrays.get(min.row).get(min.col + 1), min.row, min.col + 1));
            }
            list.add(min.value);
        }

        return list;
    }

}
