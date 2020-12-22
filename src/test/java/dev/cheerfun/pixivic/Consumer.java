package dev.cheerfun.pixivic;

import com.google.common.io.Files;
import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.transform.ColorConversionTransform;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.broadcast.BroadcastSubOp;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2RGB;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/16 3:01 下午
 * @description Consumer
 */
public class Consumer {
    public static void main(String[] args) throws Exception {

        System.out.println(Period.between(LocalDate.of(2020, 12, 22), LocalDate.of(2020, 12, 22)).getDays());

    }


}

@Data
class Predictions {
    Float[][] predictions;

}
