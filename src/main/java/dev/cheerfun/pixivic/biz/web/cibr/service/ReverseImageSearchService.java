package dev.cheerfun.pixivic.biz.web.cibr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.cibr.dto.Predictions;
import dev.cheerfun.pixivic.biz.web.cibr.mapper.CBIRMapper;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ImageUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
@Slf4j
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
        log.info("开始调用tf-serving");
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

        int l = (int) (System.currentTimeMillis() % 4);
        if (l == 0) {
            imageUrl = imageUrl.replace("https://i.pximg.net", "http://172.245.36.203:808");
        } else if (l == 1) {
            imageUrl = imageUrl.replace("https://i.pximg.net", "http://107.175.62.148:808");

        } else if (l == 2) {
            imageUrl = imageUrl.replace("https://i.pximg.net", "http://172.245.79.32:809");

        } else if (l == 3) {
            imageUrl = imageUrl.replace("https://i.pximg.net", "http://107.173.140.148:808");
        }
        long l2 = System.currentTimeMillis();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .header("Referer", "https://pixivic.com")
                .build();
        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() == 200) {
            InputStream imageFileInputStream = response.body();
            System.out.println("本次下载耗时" + (System.currentTimeMillis() - l2) / 1000 + "秒");
            try {
                ;
                long l1 = System.currentTimeMillis();
                INDArray indArray = loader.asMatrix(imageFileInputStream, false);
                System.out.println("本次预处理耗时" + (System.currentTimeMillis() - l1) / 1000 + "秒");
                Float[] feature = generateImageFeature(indArray);
                System.out.println("本次抽取耗时" + (System.currentTimeMillis() - l1) / 1000d + "秒");
                return objectMapper.writeValueAsString(feature);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public void saveFeatureToDb(Integer illustId, Integer imagePage, String feature) {
        cbirMapper.insertFeature(illustId, imagePage, feature);
    }

}
