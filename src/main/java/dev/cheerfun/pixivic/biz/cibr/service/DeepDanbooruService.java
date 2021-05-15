
package dev.cheerfun.pixivic.biz.cibr.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.cibr.dto.Predictions;
import dev.cheerfun.pixivic.biz.cibr.mapper.DeepDanbooruMapper;
import dev.cheerfun.pixivic.biz.cibr.po.FeatureTag;
import dev.cheerfun.pixivic.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.transform.ColorConversionTransform;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.Pipe;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2RGB;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/11/30 9:28 PM
 * @description DeepDanbooruService
 */
//@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeepDanbooruService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final DeepDanbooruMapper deepDanbooruMapper;
    @Value("${TFServingServer}")
    private String TFServingServer;
    @Value("${cbir.imageTempPath}")
    private String imageTempPath;

    public List<String> generateImageTagList(String imageUrl) throws IOException, InterruptedException {
        InputStream inputStream;
        if (!imageUrl.startsWith("http")) {
            inputStream = new FileInputStream(imageTempPath + "/" + imageUrl);
        } else {
            HttpRequest imageRequest = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .build();
            inputStream = httpClient.send(imageRequest, HttpResponse.BodyHandlers.ofInputStream()).body();
        }
        try {
            INDArray indArray = new NativeImageLoader(512, 512, 3, new ColorConversionTransform(COLOR_BGR2RGB)).asMatrix(inputStream, false).div(255);
            return generateImageTagList(indArray);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // 关闭流
                } catch (IOException e) {
                    log.error("inputStream close IOException:" + e.getMessage());
                }
            }
        }
        throw new BaseException(HttpStatus.BAD_REQUEST, "无效图片链接");
    }

    public List<String> generateImageTagList(INDArray indArray) throws IOException, InterruptedException {
        URI uri = URI.create("http://" + TFServingServer + "/v1/models/deepdanbooru:predict");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri).POST(HttpRequest.BodyPublishers.ofString("{\"instances\":" + indArray.toStringFull() + "}")).build();
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        Predictions predictions = objectMapper.readValue(body, Predictions.class);
        Float[] prediction = predictions.getPredictions()[0];
        return IntStream.range(0, 7722).parallel().filter(e -> prediction[e] > 0.6).mapToObj(e -> queryTagListByIndex(e + 1)
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public OutputStream zoomPic(OutputStream os, InputStream is, String contentType, Integer width, Integer height)
            throws IOException, InterruptedException, IM4JavaException {
        IMOperation op = buildIMOperation(contentType, width, height);
        Pipe pipeIn = new Pipe(is, null);
        Pipe pipeOut = new Pipe(null, os);
        ConvertCmd cmd = new ConvertCmd(true);
        cmd.setInputProvider(pipeIn);
        cmd.setOutputConsumer(pipeOut);
        cmd.run(op);
        return os;
    }

    private IMOperation buildIMOperation(String contentType, Number width, Number height) {
        IMOperation op = new IMOperation();
        String widHeight = width + "x" + height;
        op.addImage("-"); // 命令：处输入流中读取图片
        op.addRawArgs("-size", "512x512");// 按照给定比例缩放图片
        op.addRawArgs("-thumbnail", "512x512^"); // 缩放参考位置 对图像进行定位
        op.addRawArgs("-gravity", "center"); // 限制JPEG文件的最大尺寸
        op.addRawArgs("-extent", "512x512");
        op.addRawArgs("+profile", "*");// 去除Exif信息

        // 设置图片压缩格式
        op.addImage(contentType.substring(contentType.indexOf("/") + 1) + ":-");
        return op;
    }

    @Cacheable("queryTagListByIndex")
    public String queryTagListByIndex(Integer index) {
        if (index > 7719) {
            return null;
        }
        FeatureTag featureTag = deepDanbooruMapper.queryTagListByIndex(index);
        if (featureTag != null) {
            featureTag.setPixivTags(objectMapper.convertValue(featureTag.getPixivTags(), new TypeReference<List<String>>() {
            }));
            if (featureTag.getPixivTags() != null && featureTag.getPixivTags().size() > 0) {
                return featureTag.getPixivTags().get(0);
            } else {
                return featureTag.getContent();
            }
        }
        return null;
    }

}

