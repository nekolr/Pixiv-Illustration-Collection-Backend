package dev.cheerfun.pixivic;

import com.google.common.collect.Lists;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-06-28 23:05
 * @description
 */
public class RunTest {
    //构造Interger数
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        LocalDate yesterday = LocalDate.now().plusDays(-1);
        //读取日志
        try (Stream<String> stream = Files.lines(Paths.get("/Users/oysterqaq/Desktop/" + "2020-03-12" + ".log"), StandardCharsets.ISO_8859_1)) {
            //逐行处理
            stream.map(line -> {
                //搜索api
                if (line.contains("\"request\": \"GET /illustrations?")) {
                    //提取参数并且过滤
                    String params = line.substring(line.indexOf("GET ") + 4, line.indexOf(" HTTP"));
                    MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString(params).build().getQueryParams();
                    String keyword = queryParams.getFirst("keyword");
                    return URLDecoder.decode(keyword.replaceAll("%(?![0-9a-fA-F]{2})", "%25"));
                }
                return null;
            }).filter(e -> {
                //  System.out.println(e);
                return e != null && !"".equals(e) && !e.contains("*");
            }).forEach(System.out::println);
        }

        Map<String, Long> sortedWCList = Files
                .lines(Paths.get("/Users/oysterqaq/Desktop/" + yesterday + ".log"), StandardCharsets.UTF_8)
                .map(line -> {
                    if (line.contains("\"request\": \"GET /illustrations?")) {
                        String params = line.substring(line.indexOf("GET ") + 4, line.indexOf(" HTTP"));
                        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString(params).build().getQueryParams();
                        String keyword = queryParams.getFirst("keyword");
                        return URLDecoder.decode(keyword);
                    }
                    return null;
                }).filter(e -> {
                    System.out.println(e);
                    return e != null && !"null".equals(e) && !e.contains("*");
                }).collect(groupingBy(Function.identity(), counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> {
                            throw new IllegalStateException();
                        },
                        LinkedHashMap::new
                ));
        System.out.println(sortedWCList);

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
