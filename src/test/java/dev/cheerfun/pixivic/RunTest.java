package dev.cheerfun.pixivic;

import com.huaban.analysis.jieba.JiebaSegmenter;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-06-28 23:05
 * @description
 */
public class RunTest {
    public static void main(String[] args) {
        // define the limit 1 time per 10 minute
        JiebaSegmenter segmenter = new JiebaSegmenter();
        String str = "中共独裁" ;
        System.out.println(new Date());
        //System.out.println(segmenter.process(str, JiebaSegmenter.SegMode.INDEX).toString());
        IndexAnalysis.parse(str).forEach(System.out::println);
        System.out.println(new Date());
    }

}
