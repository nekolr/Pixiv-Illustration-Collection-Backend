package dev.cheerfun.pixivic.basic.sensitive.util;

import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.basic.sensitive.domain.SensitiveNode;
import dev.cheerfun.pixivic.basic.sensitive.domain.StringPointer;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.NavigableSet;

@Component
@Data
public class SensitiveFilter {

    /**
     * 为2的n次方，考虑到敏感词大概在10k左右，
     * 这个数量应为词数的数倍，使得桶很稀疏
     * 提高不命中时hash指向null的概率，
     * 加快访问速度。
     */
    final int DEFAULT_INITIAL_CAPACITY = 131072;
    private final long serialVersionUID = 1L;
    /**
     * 类似HashMap的桶，比较稀疏。
     * 使用2个字符的hash定位。
     */
    protected SensitiveNode[] nodes = new SensitiveNode[DEFAULT_INITIAL_CAPACITY];
    @Value("${sensitiveWordList.path}")
    private String path;

    public static boolean isBaseType(Object object) {
        Class className = object.getClass();
        if (className.equals(java.lang.Integer.class) ||
                className.equals(java.lang.Byte.class) ||
                className.equals(java.lang.Long.class) ||
                className.equals(java.lang.Double.class) ||
                className.equals(java.lang.Float.class) ||
                className.equals(java.lang.Character.class) ||
                className.equals(java.lang.Short.class) ||
                className.equals(java.lang.Boolean.class)) {
            return true;
        }
        return false;
    }

    @PostConstruct
    public void init() throws IOException {
        System.out.println(new Date() + " 开始装载敏感词字典");
        Files.list(Paths.get(path))
                .forEach(e -> {
                    try (BufferedReader br = new BufferedReader(new FileReader(e.toString()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            put(line);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
        System.out.println(new Date() + "装载完毕");
    }

    /**
     * 增加一个敏感词，如果词的长度（trim后）小于2，则丢弃<br/>
     * 此方法（构建）并不是主要的性能优化点。
     *
     * @param word
     * @author ZhangXiaoye
     * @date 2017年1月5日 下午2:35:21
     */
    public boolean put(String word) {
        // 长度小于2的不加入
        if (word == null || word.trim().length() < 2) {
            return false;
        }
        // 两个字符的不考虑
        if (word.length() == 2 && word.matches("\\w\\w")) {
            return false;
        }
        StringPointer sp = new StringPointer(word.trim());
        // 计算头两个字符的hash
        int hash = sp.nextTwoCharHash(0);
        // 计算头两个字符的mix表示（mix相同，两个字符相同）
        int mix = sp.nextTwoCharMix(0);
        // 转为在hash桶中的位置
        int index = hash & (nodes.length - 1);

        // 从桶里拿第一个节点
        SensitiveNode node = nodes[index];
        if (node == null) {
            // 如果没有节点，则放进去一个
            node = new SensitiveNode(mix);
            // 并添加词
            node.words.add(sp);
            // 放入桶里
            nodes[index] = node;
        } else {
            // 如果已经有节点（1个或多个），找到正确的节点
            for (; true; node = node.getNext()) {
                // 匹配节点
                if (node.headTwoCharMix == mix) {
                    node.words.add(sp);
                    return true;
                }
                // 如果匹配到最后仍然不成功，则追加一个节点
                if (node.getNext() == null) {
                    new SensitiveNode(mix, node).words.add(sp);
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * 对句子进行敏感词过滤<br/>
     * 如果无敏感词返回输入的sentence对象，即可以用下面的方式判断是否有敏感词：<br/><code>
     * String result = filter.filter(sentence, '*');<br/>
     * if(result != sentence){<br/>
     * &nbsp;&nbsp;// 有敏感词<br/>
     * }
     * </code>
     *
     * @param sentence 句子
     * @return 过滤后的句子
     * @author ZhangXiaoye
     * @date 2017年1月5日 下午4:16:31
     */
    public String filter(String sentence) {
        if (sentence.length() == 0) {
            return sentence;
        }
        return filter(sentence, '*');
    }

    public String filter(String sentence, char replace) {
        // 先转换为StringPointer
        StringPointer sp = new StringPointer(sentence);

        // 标示是否替换
        boolean replaced = false;

        // 匹配的起始位置
        int i = 0;
        while (i < sp.length - 1) {
            /*
             * 移动到下一个匹配位置的步进：
             * 如果未匹配为1，如果匹配是匹配的词长度
             */
            int step = 1;
            // 计算此位置开始2个字符的hash
            int hash = sp.nextTwoCharHash(i);
            /*
             * 根据hash获取第一个节点，
             * 真正匹配的节点可能不是第一个，
             * 所以有后面的for循环。
             */
            SensitiveNode node = nodes[hash & (nodes.length - 1)];
            /*
             * 如果非敏感词，node基本为null。
             * 这一步大幅提升效率
             */
            if (node != null) {
                /*
                 * 如果能拿到第一个节点，
                 * 才计算mix（mix相同表示2个字符相同）。
                 * mix的意义和HashMap先hash再equals的equals部分类似。
                 */
                int mix = sp.nextTwoCharMix(i);
                /*
                 * 循环所有的节点，如果非敏感词，
                 * mix相同的概率非常低，提高效率
                 */
                outer:
                for (; node != null; node = node.getNext()) {
                    /*
                     * 对于一个节点，先根据头2个字符判断是否属于这个节点。
                     * 如果属于这个节点，看这个节点的词库是否命中。
                     * 此代码块中访问次数已经很少，不是优化重点
                     */
                    if (node.headTwoCharMix == mix) {
                        /*
                         * 查出比剩余sentence小的最大的词。
                         * 例如剩余sentence为"色情电影哪家强？"，
                         * 这个节点含三个词从小到大为："色情"、"色情电影"、"色情信息"。
                         * 则从“色情电影”开始向前匹配
                         */
                        NavigableSet<StringPointer> desSet = node.words.headSet(sp.substring(i), true);
                        for (StringPointer word : desSet.descendingSet()) {
                            /*
                             * 仍然需要再判断一次，例如"色情信息哪里有？"，
                             * 如果节点只包含"色情电影"一个词，
                             * 仍然能够取到word为"色情电影"，但是不该匹配。
                             */
                            if (sp.nextStartsWith(i, word)) {
                                // 匹配成功，将匹配的部分，用replace制定的内容替代
                                sp.fill(i, i + word.length, replace);
                                // 跳过已经替代的部分
                                step = word.length;
                                // 标示有替换
                                replaced = true;
                                // 跳出循环（然后是while循环的下一个位置）
                                break outer;
                            }
                        }

                    }
                }
            }

            // 移动到下一个匹配位置
            i += step;
        }

        // 如果没有替换，直接返回入参（节约String的构造copy）
        if (replaced) {
            return sp.toString();
        } else {
            return sentence;
        }
    }

    public Object filter(Object object) {
        System.out.println(object);
        if (object instanceof String) {
            return filter((String) object);
        } else if (!isBaseType(object)) {
            //如果是对象则遍历里面的属性
            Class<?> valueClass = object.getClass();
            //集合则遍历元素
            if (object instanceof Collection) {
                ((Collection<?>) object).forEach(this::filter);
                return object;
            }
            for (Field field : valueClass.getDeclaredFields()) {
                if (Arrays.stream(field.getDeclaredAnnotations()).anyMatch(e -> e.annotationType().equals(SensitiveCheck.class))) {
                    field.setAccessible(true);
                    //递归处理
                    Object o = null;
                    try {
                        o = field.get(object);
                        if (o != null) {
                            field.set(object, filter(o));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            return object;
        }
        return object;
    }

}
