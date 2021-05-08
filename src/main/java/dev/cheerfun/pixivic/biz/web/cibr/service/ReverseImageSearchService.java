package dev.cheerfun.pixivic.biz.web.cibr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.cibr.dto.Predictions;
import dev.cheerfun.pixivic.biz.web.cibr.mapper.CBIRMapper;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ImageUrl;
import lombok.RequiredArgsConstructor;
import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/6 6:14 PM
 * @description ReverseImageSearchService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReverseImageSearchService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final IllustrationBizService illustrationBizService;
    private final MilvusService milvusService;
    private final CBIRMapper cbirMapper;
    @Value("${TFServingServer}")
    private String TFServingServer;
    NativeImageLoader loader = new NativeImageLoader(224, 224, 3);


    public List<Illustration> searchTopKIllustId(MultipartFile file) throws IOException, InterruptedException {
        INDArray image = loader.asMatrix(file.getInputStream(), false);
        List<Integer> illustIdList = searchTopKIllustIdFromMilvus(generateImageFeature(image), 10);
        return illustrationBizService.queryIllustrationByIdList(illustIdList);
    }

    //从Milvus中检索topk相似向量
    public List<Integer> searchTopKIllustIdFromMilvus(Float[] imageFeature, Integer k) {
        return milvusService.search(Arrays.asList(imageFeature), k);
    }

    //生成特征向量
    public Float[] generateImageFeature(INDArray image) throws IOException, InterruptedException {
        URI uri = URI.create("http://" + TFServingServer + "/v1/models/vgg:predict");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri).POST(HttpRequest.BodyPublishers.ofString("{\"instances\":" + image.toStringFull() + "}")).build();
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        Predictions predictions = objectMapper.readValue(body, Predictions.class);
        return predictions.getPredictions()[0];
    }

    public Boolean illustFeatureExtraction(Illustration illustration) throws IOException, InterruptedException {
        List<ImageUrl> imageUrls = illustration.getImageUrls();
        for (int i = 0; i < imageUrls.size(); i++) {
            String feature = imageFeatureExtraction(imageUrls.get(i).getMedium());
            saveFeatureToDb(illustration.getId(), i, feature);
        }
        return true;
    }

    public String imageFeatureExtraction(String imageUrl) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .header("Referer", "https://pixivic.com")
                .build();
        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() == 200) {
            InputStream imageFileInputStream = response.body();
            Float[] feature = generateImageFeature(loader.asMatrix(imageFileInputStream, false));
            return objectMapper.writeValueAsString(feature);
        } else {
            return "";
        }
    }

    public void saveFeatureToDb(Integer illustId, Integer imagePage, String feature) {
        cbirMapper.insertFeature(illustId, imagePage, feature);
    }

}
