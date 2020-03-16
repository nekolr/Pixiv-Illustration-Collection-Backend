package dev.cheerfun.pixivic;

import com.google.common.collect.Lists;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-06-28 23:05
 * @description
 */
public class RunTest {
    //构造Interger数
    public static void main(String[] args) throws IOException, TimeoutException {
        String a = "";
        int i = a.indexOf("");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.93.4.88");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("root");
        Connection connection = factory.newConnection();
        //创建连接
        Channel channel = connection.createChannel();
        //创建信道//创建一个type="direct"、持久化的、非自动删除的交换器
        channel.exchangeDeclare("EXCHANGE_DEMO", "direct", true, false, null);
        //创建一个持久化、非排他的、非自动删除的队列
        channel.queueDeclare("QUEUE_DEMO", true, false, false, null);
        //将交换器与队列通过路由键绑定
        channel.queueBind("QUEUE_DEMO", "EXCHANGE_DEMO", "ROUTING_KEY_DEMO");
        //发送一条持久化的消息:hello world!
        String message = "Hello World!";
        channel.basicPublish("EXCHANGE_DEMO", "ROUTING_KEY_DEMO", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        channel.close();
        connection.close();
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
