package dev.cheerfun.pixivic;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.BaseEncoding;
import com.jdcloud.sdk.auth.CredentialsProvider;
import com.jdcloud.sdk.auth.StaticCredentialsProvider;
import com.jdcloud.sdk.http.HttpRequestConfig;
import com.jdcloud.sdk.http.Protocol;
import com.jdcloud.sdk.service.clouddnsservice.client.ClouddnsserviceClient;
import com.jdcloud.sdk.service.clouddnsservice.model.*;
import com.squareup.okhttp.*;

import dev.cheerfun.pixivic.biz.cibr.dto.Predictions;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
        CredentialsProvider credentialsProvider = new StaticCredentialsProvider("5A84A85CD2C0379657E1C8A6140A079C", "93173B7EA351CB7E99D99C7D1E90E479");
        ClouddnsserviceClient clouddnsserviceClient = ClouddnsserviceClient.builder()
                .credentialsProvider(credentialsProvider)
                .httpRequestConfig(new HttpRequestConfig.Builder().protocol(Protocol.HTTPS).build()) //默认为HTTPS
                .build();
        GetDomainsResponse domains = clouddnsserviceClient.getDomains(new GetDomainsRequest().pageNumber(1).pageSize(40).regionId("cn-north-1"));
        SearchRRResponse searchRRResponse = clouddnsserviceClient.searchRR(new SearchRRRequest().domainId("4304").regionId("cn-north-1").pageNumber(2).pageSize(10));
        SearchRRResult result = searchRRResponse.getResult();
        List<RR> dataList = result.getDataList();
        dataList.stream().filter(e -> {
            return e.getId() == 32622660 || e.getId() == 26235035 || e.getId() == 26235034;
        }).map(e -> {
                    UpdateRR updateRR = new UpdateRR();

                    updateRR.id(e.getId()).hostRecord(e.getHostRecord()).hostValue(e.getHostValue()).type(e.getType()).viewValue(e.getViewValue().get(0)).ttl(60).weight(2);
                    return updateRR;
                }

        ).forEach(
                e -> {

                    clouddnsserviceClient.updateRR(new UpdateRRRequest().req(e).domainId("4304").regionId("cn-north-1"));

                }
        );

        System.out.println(searchRRResponse);
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
