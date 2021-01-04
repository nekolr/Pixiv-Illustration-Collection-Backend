package dev.cheerfun.pixivic;

import com.google.common.io.BaseEncoding;
import com.google.common.io.Files;
import dev.cheerfun.pixivic.biz.web.vip.po.ExchangeCode;
import dev.cheerfun.pixivic.common.util.encrypt.CRC8;
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
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dev.cheerfun.pixivic.common.util.encrypt.ChaCha20.chacha20Decrypt;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2RGB;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/16 3:01 下午
 * @description Consumer
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        System.out.println();
        String s = UUID.randomUUID().toString() + "#123";
        System.out.println(s
                .substring(s.indexOf("#") + 1));
        System.out.println(Period.between(LocalDate.of(2020, 12, 22), LocalDate.of(2020, 12, 22)).getDays());
        System.out.println(validateExchangeCode("PNRJ7KVAHFHXGYD7"));
    }

    public static ExchangeCode validateExchangeCode(String exchangeCode) {
        byte[] decode = BaseEncoding.base32().decode(exchangeCode);
        //首先校验 校验和
        byte c = CRC8.calcCrc8(decode, 0, 9);
        if (c == decode[9]) {
            //解密
            byte[] ciphertext = new byte[9];
            System.arraycopy(decode, 0, ciphertext, 0, ciphertext.length);
            byte[] payload = chacha20Decrypt(ciphertext, "生蚝是位兴趣使然的OTAKU".getBytes(StandardCharsets.UTF_8), "PIXIVICJUST4".getBytes(StandardCharsets.UTF_8), 21);
            //得到编号
            byte[] codeId = new byte[4];
            codeId[0] = payload[0];
            codeId[1] = payload[2];
            codeId[2] = payload[4];
            codeId[3] = payload[6];
            ByteBuffer wrapped = ByteBuffer.wrap(codeId, 0, 4);
            return new ExchangeCode(wrapped.getInt(), payload[8]);
        }
        return null;
    }
}

@Data
class Predictions {
    Float[][] predictions;

}
