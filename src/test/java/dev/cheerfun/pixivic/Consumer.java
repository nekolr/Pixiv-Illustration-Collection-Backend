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
import java.time.LocalDateTime;
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
     /*   NativeImageLoader loader = new NativeImageLoader(224, 224, 3, new ColorConversionTransform(COLOR_BGR2RGB));

        INDArray indArray = loader.asMatrix(new File("/Users/oysterqaq/Desktop/3.jpg"), false);
        System.out.println(Arrays.toString(indArray.shape()));
        System.out.println(indArray);
        DataNormalization scaler = new VGG16ImagePreProcessor();
        // scaler.transform(indArray);
        INDArray mean = Nd4j.create(new double[]{103.939, 116.779, 123.68}).castTo(DataType.FLOAT);
        Nd4j.getExecutioner().execAndReturn(new BroadcastSubOp(indArray.dup(), mean, indArray, 3));
        System.out.println(indArray);
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://manjaro:8501/v1/models/vgg:predict");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri).POST(HttpRequest.BodyPublishers.ofString("{\"instances\":"+indArray.toStringFull()+"}")).build();
          String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
       System.out.println(body);
        Predictions predictions = new ObjectMapper().readValue(body, Predictions.class);
        System.out.println(predictions.predictions[0].length);
*/

        NativeImageLoader loader = new NativeImageLoader(512, 512, 3, new ColorConversionTransform(COLOR_BGR2RGB));

        INDArray indArray = loader.asMatrix(new File("/Users/oysterqaq/Desktop/2.jpg"), false).div(255);
        System.out.println(Arrays.toString(indArray.shape()));
        // DataNormalization scaler = new VGG16ImagePreProcessor();
        // scaler.transform(indArray);
        // INDArray mean = Nd4j.create(new double[]{103.939, 116.779, 123.68}).castTo(DataType.FLOAT);
        //  Nd4j.getExecutioner().execAndReturn(new BroadcastSubOp(indArray.dup(), mean, indArray, 3));
        System.out.println(indArray);
        HttpClient httpClient = HttpClient.newHttpClient();
        String body = null;
        for (int i = 0; i < 10; i++) {
            System.out.println(LocalDateTime.now());
            URI uri = URI.create("http://manjaro:8501/v1/models/deepdanbooru:predict");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri).POST(HttpRequest.BodyPublishers.ofString("{\"instances\":" + indArray.toStringFull() + "}")).build();
            body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println(LocalDateTime.now());

        }

        System.out.println(body);
        Predictions predictions = new ObjectMapper().readValue(body, Predictions.class);
        List<String> strings = Files.readLines(new File("/Users/oysterqaq/Desktop/deepdanbooru-v4-20200814-sgd-e30/tags.txt"), Charset.defaultCharset());
        for (Float[] prediction : predictions.predictions) {
            IntStream.range(0, prediction.length).forEach(e -> {
                if (prediction[e] > 0.6) {
                    System.out.println(strings.get(e) + ":" + prediction[e]);
                }
            });
            ///  System.out.println(Arrays.stream(prediction).parallel().filter(e -> e > 0.6f).collect(Collectors.toList()));
        }
        System.out.println(predictions.predictions[0].length);

    }


}

@Data
class Predictions {
    Float[][] predictions;

}
