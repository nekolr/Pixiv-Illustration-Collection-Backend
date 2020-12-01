package dev.cheerfun.pixivic.biz.web.cibr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import dev.cheerfun.pixivic.biz.web.cibr.dto.Predictions;
import dev.cheerfun.pixivic.biz.web.cibr.mapper.DeepDanbooruMapper;
import dev.cheerfun.pixivic.biz.web.cibr.po.FeatureTag;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.transform.ColorConversionTransform;
import org.gm4java.engine.support.PooledGMService;
import org.gm4java.im4java.GMBatchCommand;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.Pipe;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeepDanbooruService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final DeepDanbooruMapper deepDanbooruMapper;
    @Value("${TFServingServer}")
    private String TFServingServer;

    public List<String> generateImageTagList(MultipartFile file) throws IOException, InterruptedException, IM4JavaException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            zoomPic(outputStream, file.getInputStream(), file.getContentType(), 512, 512);
            try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                NativeImageLoader loader = new NativeImageLoader(512, 512, 3, new ColorConversionTransform(COLOR_BGR2RGB));
                INDArray indArray = loader.asMatrix(inputStream, false).div(255);
                URI uri = URI.create("http://" + TFServingServer + "/v1/models/deepdanbooru:predict");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri).POST(HttpRequest.BodyPublishers.ofString("{\"instances\":" + indArray.toStringFull() + "}")).build();
                String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
                Predictions predictions = objectMapper.readValue(body, Predictions.class);
                Float[] prediction = predictions.getPredictions()[0];
                return IntStream.range(0, 7722).parallel().filter(e -> prediction[e] > 0.6).mapToObj(e -> queryTagListByIndex(e + 1)
                ).filter(Objects::nonNull).collect(Collectors.toList());
            }
        }
    }

    public static OutputStream zoomPic(OutputStream os, InputStream is, String contentType, Integer width, Integer height)
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

    private static IMOperation buildIMOperation(String contentType, Number width, Number height) {
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

    private BufferedImage imageFromINDArray(INDArray array) {
        long[] shape = array.shape();

        long height = shape[2];
        long width = shape[3];
        BufferedImage image = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int red = array.getInt(0, 2, y, x);
                int green = array.getInt(0, 1, y, x);
                int blue = array.getInt(0, 0, y, x);

                //handle out of bounds pixel values
                red = Math.min(red, 255);
                green = Math.min(green, 255);
                blue = Math.min(blue, 255);

                red = Math.max(red, 0);
                green = Math.max(green, 0);
                blue = Math.max(blue, 0);
                image.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }
        return image;
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

    //接受图片，缩放为512，512，转为矩阵后请求tf serving
    //得到结果后过滤出0.6分以上的index，从数据库中查找对应的label，优先列出角色标签，之后显示有对应pixivtab的标签
    //用户在页面上可以点击tag来搜索

}
