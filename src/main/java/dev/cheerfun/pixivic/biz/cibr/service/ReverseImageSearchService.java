package dev.cheerfun.pixivic.biz.cibr.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.cibr.constant.MilvusInfo;
import dev.cheerfun.pixivic.biz.cibr.dto.Predictions;
import dev.cheerfun.pixivic.biz.cibr.mapper.CBIRMapper;
import dev.cheerfun.pixivic.biz.cibr.po.IllustFeature;
import dev.cheerfun.pixivic.biz.web.illust.secmapper.IllustrationBizMapper;
import dev.cheerfun.pixivic.common.exception.BaseException;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ImageUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.broadcast.BroadcastSubOp;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/6 6:14 PM
 * @description ReverseImageSearchService
 */
@Slf4j
//@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReverseImageSearchService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final IllustrationBizMapper illustrationBizMapper;
    private final MilvusService milvusService;
    private final CBIRMapper cbirMapper;
    @Value("${TFServingServer}")
    private String TFServingServer;
    @Value("${cbir.imageTempPath}")
    private String imageTempPath;

    public INDArray loadImage(InputStream inputStream) {
        try {
            INDArray image = new NativeImageLoader(224, 224, 3).asMatrix(inputStream, false);
            Nd4j.getExecutioner().execAndReturn(new BroadcastSubOp(image.dup(), Nd4j.create(new double[]{103.939, 116.779, 123.68}).castTo(DataType.FLOAT), image, 3));
            return image;
        } catch (IOException e) {
            throw new BaseException(HttpStatus.BAD_REQUEST, "加载图片出错");
        } finally {
            try {
                inputStream.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public List<Integer> searchTopKIllustId(MultipartFile file) throws IOException, InterruptedException {
        return searchTopKIllustIdFromMilvus(generateImageFeature(loadImage(file.getInputStream())), 10);
    }

    public List<Integer> searchTopKIllustId(String imageUrl) throws IOException, InterruptedException {
        if (!imageUrl.startsWith("http")) {
            return searchTopKIllustIdFromMilvus(generateImageFeature(loadImage(new FileInputStream(imageTempPath + "/" + imageUrl))), 10);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .build();
        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        try {
            return searchTopKIllustIdFromMilvus(generateImageFeature(loadImage(response.body())), 10);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }
        throw new BaseException(HttpStatus.BAD_REQUEST, "无效图片链接");
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

    public List<IllustFeature> illustFeatureExtraction(Illustration illustration) throws IOException, InterruptedException {
        List<ImageUrl> imageUrls = illustration.getImageUrls();
        List<IllustFeature> illustFeatureList = new ArrayList<>(imageUrls.size());
        for (int i = 0; i < imageUrls.size(); i++) {
            String feature = imageFeatureExtraction(imageUrls.get(i).getMedium());
            saveFeatureToDb(illustration.getId(), i, feature);
            illustFeatureList.add(new IllustFeature((long) illustration.getId(), i, feature));
        }
        return illustFeatureList;
    }

    public String imageFeatureExtraction(String imageUrl) throws IOException, InterruptedException {

        int l = (int) (System.currentTimeMillis() % 7);
        if (l == 0) {
            imageUrl = imageUrl.replace("https://i.pximg.net", "http://91.188.223.215");
        } else if (l == 1) {
            imageUrl = imageUrl.replace("https://i.pximg.net", "http://91.206.92.37");
        } else if (l == 2) {
            imageUrl = imageUrl.replace("https://i.pximg.net", "http://45.130.145.108");
        } else if (l == 3) {
            imageUrl = imageUrl.replace("https://i.pximg.net", "http://194.5.78.189");
        } else if (l == 4) {
            imageUrl = imageUrl.replace("https://i.pximg.net", "http://75.119.133.187");
        } else if (l == 5) {
            imageUrl = imageUrl.replace("https://i.pximg.net", "http://45.89.228.106");
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .header("Referer", "https://pixiv.net")
                .build();
        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() == 200) {
            InputStream imageFileInputStream = response.body();
            try {
                INDArray image = loadImage(imageFileInputStream);
                Float[] feature = generateImageFeature(image);
                return objectMapper.writeValueAsString(feature);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (imageFileInputStream != null) {
                    try {
                        imageFileInputStream.close(); // 关闭流
                    } catch (IOException e) {
                        log.error("inputStream close IOException:" + e.getMessage());
                    }
                }
            }
        } else {
            return null;
        }
    }

    public void saveFeatureToDb(Integer illustId, Integer imagePage, String feature) {
        cbirMapper.insertFeature(illustId, imagePage, feature);
    }

    public List<Illustration> queryIllustrationByIdList(List<Integer> illustId) {
        return illustId.stream().parallel().map(this::queryIllustrationByIdFromDb).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager = "SecondaryTransactionManager")
    public Illustration queryIllustrationByIdFromDb(Integer illustId) {
        Illustration illustration = illustrationBizMapper.queryIllustrationByIllustId(illustId);
        return objectMapper.convertValue(illustration, new TypeReference<Illustration>() {
        });
    }

    //TODO每日增量同步
    @Transactional
    public Boolean dailySync(List<Illustration> illustrations) {
        log.info("开始进行每日同步任务");
        illustrations.forEach(e -> {
            try {
                List<IllustFeature> illustFeatureList = illustFeatureExtraction(e);
                log.info("开始将画作：" + e.getId() + "的特征存入milvus");
                for (IllustFeature illustFeature : illustFeatureList) {
                    if (illustFeature.getFeature() != null) {
                        Float[] floats = objectMapper.readValue(illustFeature.getFeature(), Float[].class);
                        Integer sync = cbirMapper.isSync(illustFeature.getIllustId(), illustFeature.getImagePage());
                        if (sync == null) {
                            milvusService.saveIllustFeatureToMilvus(illustFeature.getIllustId(), Collections.singletonList(Arrays.asList(floats)), MilvusInfo.COLLECTION);
                            cbirMapper.finishSyncToMilvus(illustFeature.getIllustId(), illustFeature.getImagePage());
                        }
                    }
                }
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
        });
        log.info("每日同步任务结束");
        return true;
    }

    private void saveImage(INDArray indArray) throws IOException {

        File outputfile = new File("/Users/oysterqaq/Desktop/image.jpg");
        ImageIO.write(imageFromINDArray(indArray), "jpg", outputfile);
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
}
