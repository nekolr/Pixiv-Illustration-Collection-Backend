package dev.cheerfun.pixivic;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;

import java.time.LocalDate;
import java.time.Period;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/13 8:52 AM
 * @description CRC8
 */

public class CRC8 {
    static byte[] crc8_tab = {(byte) 0, (byte) 94, (byte) 188, (byte) 226, (byte) 97, (byte) 63, (byte) 221, (byte) 131, (byte) 194, (byte) 156, (byte) 126, (byte) 32, (byte) 163, (byte) 253, (byte) 31, (byte) 65, (byte) 157, (byte) 195, (byte) 33, (byte) 127, (byte) 252, (byte) 162, (byte) 64, (byte) 30, (byte) 95, (byte) 1, (byte) 227, (byte) 189, (byte) 62, (byte) 96, (byte) 130, (byte) 220, (byte) 35, (byte) 125, (byte) 159, (byte) 193, (byte) 66, (byte) 28, (byte) 254, (byte) 160, (byte) 225, (byte) 191, (byte) 93, (byte) 3, (byte) 128, (byte) 222, (byte) 60, (byte) 98, (byte) 190, (byte) 224, (byte) 2, (byte) 92, (byte) 223, (byte) 129, (byte) 99, (byte) 61, (byte) 124, (byte) 34, (byte) 192, (byte) 158, (byte) 29, (byte) 67, (byte) 161, (byte) 255, (byte) 70, (byte) 24,
            (byte) 250, (byte) 164, (byte) 39, (byte) 121, (byte) 155, (byte) 197, (byte) 132, (byte) 218, (byte) 56, (byte) 102, (byte) 229, (byte) 187, (byte) 89, (byte) 7, (byte) 219, (byte) 133, (byte) 103, (byte) 57, (byte) 186, (byte) 228, (byte) 6, (byte) 88, (byte) 25, (byte) 71, (byte) 165, (byte) 251, (byte) 120, (byte) 38, (byte) 196, (byte) 154, (byte) 101, (byte) 59, (byte) 217, (byte) 135, (byte) 4, (byte) 90, (byte) 184, (byte) 230, (byte) 167, (byte) 249, (byte) 27, (byte) 69, (byte) 198, (byte) 152, (byte) 122, (byte) 36, (byte) 248, (byte) 166, (byte) 68, (byte) 26, (byte) 153, (byte) 199, (byte) 37, (byte) 123, (byte) 58, (byte) 100, (byte) 134, (byte) 216, (byte) 91, (byte) 5, (byte) 231, (byte) 185, (byte) 140, (byte) 210, (byte) 48, (byte) 110, (byte) 237,
            (byte) 179, (byte) 81, (byte) 15, (byte) 78, (byte) 16, (byte) 242, (byte) 172, (byte) 47, (byte) 113, (byte) 147, (byte) 205, (byte) 17, (byte) 79, (byte) 173, (byte) 243, (byte) 112, (byte) 46, (byte) 204, (byte) 146, (byte) 211, (byte) 141, (byte) 111, (byte) 49, (byte) 178, (byte) 236, (byte) 14, (byte) 80, (byte) 175, (byte) 241, (byte) 19, (byte) 77, (byte) 206, (byte) 144, (byte) 114, (byte) 44, (byte) 109, (byte) 51, (byte) 209, (byte) 143, (byte) 12, (byte) 82, (byte) 176, (byte) 238, (byte) 50, (byte) 108, (byte) 142, (byte) 208, (byte) 83, (byte) 13, (byte) 239, (byte) 177, (byte) 240, (byte) 174, (byte) 76, (byte) 18, (byte) 145, (byte) 207, (byte) 45, (byte) 115, (byte) 202, (byte) 148, (byte) 118, (byte) 40, (byte) 171, (byte) 245, (byte) 23, (byte) 73, (byte) 8,
            (byte) 86, (byte) 180, (byte) 234, (byte) 105, (byte) 55, (byte) 213, (byte) 139, (byte) 87, (byte) 9, (byte) 235, (byte) 181, (byte) 54, (byte) 104, (byte) 138, (byte) 212, (byte) 149, (byte) 203, (byte) 41, (byte) 119, (byte) 244, (byte) 170, (byte) 72, (byte) 22, (byte) 233, (byte) 183, (byte) 85, (byte) 11, (byte) 136, (byte) 214, (byte) 52, (byte) 106, (byte) 43, (byte) 117, (byte) 151, (byte) 201, (byte) 74, (byte) 20, (byte) 246, (byte) 168, (byte) 116, (byte) 42, (byte) 200, (byte) 150, (byte) 21, (byte) 75, (byte) 169, (byte) 247, (byte) 182, (byte) 232, (byte) 10, (byte) 84, (byte) 215, (byte) 137, (byte) 107, 53};

    /**
     * 计算数组的CRC8校验值
     *
     * @param data 需要计算的数组
     * @return CRC8校验值
     */
    public static byte calcCrc8(byte[] data) {
        return calcCrc8(data, 0, data.length, (byte) 0);
    }

    /**
     * 计算CRC8校验值
     *
     * @param data   数据
     * @param offset 起始位置
     * @param len    长度
     * @return 校验值
     */
    public static byte calcCrc8(byte[] data, int offset, int len) {
        return calcCrc8(data, offset, len, (byte) 0);
    }

    /**
     * 计算CRC8校验值
     *
     * @param data   数据
     * @param offset 起始位置
     * @param len    长度
     * @param preval 之前的校验值
     * @return 校验值
     */
    public static byte calcCrc8(byte[] data, int offset, int len, byte preval) {
        byte ret = preval;
        for (int i = offset; i < (offset + len); ++i) {
            ret = crc8_tab[(0x00ff & (ret ^ data[i]))];
        }
        return ret;
    }

    // 测试
    public static void main(String[] args) {
        try {
            /* 必要步骤：
             * 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
             * 这里采用的是从环境变量读取的方式，需要在环境变量中先设置这两个值。
             * 你也可以直接在代码中写死密钥对，但是小心不要将代码复制、上传或者分享给他人，
             * 以免泄露密钥对危及你的财产安全。
             * CAM密匙查询: https://console.cloud.tencent.com/cam/capi*/
            Credential cred = new Credential("AKIDBDxTUHQDNI1rStw2t1wgV4kCeqnV5A85", "qkPpysWuFLciZAVaQ7kHYZMKU5T9Xcqp");

            // 实例化一个http选项，可选，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();

            /* SDK默认使用POST方法。
             * 如果你一定要使用GET方法，可以在这里设置。GET方法无法处理一些较大的请求 */
            httpProfile.setReqMethod("POST");
            /* SDK有默认的超时时间，非必要请不要进行调整
             * 如有需要请在代码中查阅以获取最新的默认值 */
            httpProfile.setConnTimeout(60);
            /* SDK会自动指定域名。通常是不需要特地指定域名的，但是如果你访问的是金融区的服务
             * 则必须手动指定域名，例如sms的上海金融区域名： sms.ap-shanghai-fsi.tencentcloudapi.com */
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            /* 非必要步骤:
             * 实例化一个客户端配置对象，可以指定超时时间等配置 */
            ClientProfile clientProfile = new ClientProfile();
            /* SDK默认用TC3-HMAC-SHA256进行签名
             * 非必要请不要修改这个字段 */
            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);
            /* 实例化要请求产品(以sms为例)的client对象
             * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，或者引用预设的常量 */
            SmsClient client = new SmsClient(cred, "", clientProfile);
            /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
             * 你可以直接查询SDK源码确定接口有哪些属性可以设置
             * 属性可能是基本类型，也可能引用了另一个数据结构
             * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明 */
            SendSmsRequest req = new SendSmsRequest();

            /* 填充请求参数,这里request对象的成员变量即对应接口的入参
             * 你可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
             * 基本类型的设置:
             * 帮助链接：
             * 短信控制台: https://console.cloud.tencent.com/sms/smslist
             * sms helper: https://cloud.tencent.com/document/product/382/3773 */

            /* 短信应用ID: 短信SdkAppid在 [短信控制台] 添加应用后生成的实际SdkAppid，示例如1400006666 */
            String appid = "1400478972";
            req.setSmsSdkAppid(appid);

            /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，签名信息可登录 [短信控制台] 查看 */
            String sign = "御宅电波";
            req.setSign(sign);


            /* 模板 ID: 必须填写已审核通过的模板 ID。模板ID可登录 [短信控制台] 查看 */
            String templateID = "858513";
            req.setTemplateID(templateID);

            /* 下发手机号码，采用 e.164 标准，+[国家或地区码][手机号]
             * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号*/
            String[] phoneNumbers = {"+8613489155494"};
            req.setPhoneNumberSet(phoneNumbers);

            /* 模板参数: 若无模板参数，则设置为空*/
            String[] templateParams = {"51678"};
            req.setTemplateParamSet(templateParams);

            /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
             * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
            SendSmsResponse res = client.SendSms(req);
            client.SendSms(req);

            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(res));

            // 也可以取出单个值，你可以通过官网接口文档或跳转到response对象的定义处查看返回字段的定义
            System.out.println(res.getRequestId());

        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
    }
}
