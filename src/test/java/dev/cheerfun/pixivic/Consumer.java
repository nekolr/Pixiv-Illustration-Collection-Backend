package dev.cheerfun.pixivic;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.BaseEncoding;
import com.squareup.okhttp.*;
import dev.cheerfun.pixivic.biz.web.cibr.dto.Predictions;
import dev.cheerfun.pixivic.biz.web.vip.po.ExchangeCode;
import dev.cheerfun.pixivic.common.util.encrypt.CRC8;
import lombok.Data;
import org.buildobjects.process.ProcBuilder;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.transform.ColorConversionTransform;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.broadcast.BroadcastSubOp;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static dev.cheerfun.pixivic.common.util.encrypt.ChaCha20.chacha20Decrypt;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2RGB;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2BGR;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/16 3:01 下午
 * @description Consumer
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        NativeImageLoader loader = new NativeImageLoader(224, 224, 3, new ColorConversionTransform(COLOR_RGB2BGR));
        INDArray path = loader.asMatrix(new File("/Users/oysterqaq/Desktop/Snipaste_2021-05-06_19-40-27.jpg"), false);

        Nd4j.getExecutioner().execAndReturn(new BroadcastSubOp(path.dup(), Nd4j.create(new double[]{103.939, 116.779, 123.68}).castTo(DataType.FLOAT), path, 3));
        System.out.println(path);
        URI uri = URI.create("http://" + "manjaro:8501" + "/v1/models/vgg:predict");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri).POST(HttpRequest.BodyPublishers.ofString("{\"instances\":" + path.toStringFull() + "}")).build();
        String body = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString()).body();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Predictions predictions = objectMapper.readValue(body, Predictions.class);
        Float[] prediction = predictions.getPredictions()[0];
        System.out.println(new ObjectMapper().writeValueAsString(prediction));

/*        NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
        INDArray image = loader.asMatrix(new File("/Users/oysterqaq/Desktop/Snipaste_2021-05-06_19-40-27.jpg"));
        ZooModel zooModel = VGG16.builder().build();
        ComputationGraph vgg16=   (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        // Mean subtraction pre-processing step for VGG
        DataNormalization scaler = new VGG16ImagePreProcessor();
        scaler.transform(image);

        //Call the feedForward method to get a map of activations for each layer
       // System.out.println(vgg16.feedForward(image, false));
        Map<String, INDArray> stringINDArrayMap = vgg16.feedForward(image, false);
        System.out.println();
        System.out.println(vgg16.feedForward(image, false));*/

    }

    public static void download(String url, String savePath) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("referer", "https://pixiv.net")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36")
                .url(url)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        URL u = new URL(url);
        InputStream is = null;
        try {
            is = response.body().byteStream();
            // 将流写入到文件
            Path path = Path.of(savePath, u.getFile());
            Files.createDirectories(path.getParent());
            Files.write(path, is.readAllBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
