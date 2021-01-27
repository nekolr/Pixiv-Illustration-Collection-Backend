/*
package dev.cheerfun.pixivic.common.util.message;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.cvm.v20170312.CvmClient;
import com.tencentcloudapi.cvm.v20170312.models.DescribeInstancesRequest;
import com.tencentcloudapi.cvm.v20170312.models.DescribeInstancesResponse;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import dev.cheerfun.pixivic.common.po.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

*/
/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/27 10:01 AM
 * @description MessageUtil
 *//*

@Component
@Slf4j
public class MessageUtil {
    private LinkedBlockingQueue<Email> waitForSendQueue;
    @Autowired
    private ExecutorService messageExecutorService;

    public static void main(String[] args) {
        try {
            Credential cred = new Credential("secretId", "secretKey");
            CvmClient client = new CvmClient(cred, "ap-shanghai");

            */
/* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
 * 你可以直接查询SDK源码确定接口有哪些属性可以设置
 * 属性可能是基本类型，也可能引用了另一个数据结构
 * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明 *//*

            SendSmsRequest req = new SendSmsRequest();

            */
/* 填充请求参数,这里request对象的成员变量即对应接口的入参
 * 你可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
 * 基本类型的设置:
 * 帮助链接：
 * 短信控制台: https://console.cloud.tencent.com/sms/smslist
 * sms helper: https://cloud.tencent.com/document/product/382/3773 *//*


 */
/* 短信应用ID: 短信SdkAppid在 [短信控制台] 添加应用后生成的实际SdkAppid，示例如1400006666 *//*

            String appid = "1400009099";
            req.setSmsSdkAppid(appid);

            */
/* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，签名信息可登录 [短信控制台] 查看 *//*

            String sign = "签名内容";
            req.setSign(sign);

            */
/* 国际/港澳台短信 senderid: 国内短信填空，默认未开通，如需开通请联系 [sms helper] *//*

            String senderid = "xxx";
            req.setSenderId(senderid);

            */
/* 用户的 session 内容: 可以携带用户侧 ID 等上下文信息，server 会原样返回 *//*

            String session = "xxx";
            req.setSessionContext(session);

            */
/* 短信码号扩展号: 默认未开通，如需开通请联系 [sms helper] *//*

            String extendcode = "xxx";
            req.setExtendCode(extendcode);

            */
/* 模板 ID: 必须填写已审核通过的模板 ID。模板ID可登录 [短信控制台] 查看 *//*

            String templateID = "400000";
            req.setTemplateID(templateID);

            */
/* 下发手机号码，采用 e.164 标准，+[国家或地区码][手机号]
 * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号*//*

            String[] phoneNumbers = {"+8621212313123", "+8612345678902", "+8612345678903"};
            req.setPhoneNumberSet(phoneNumbers);

            */
/* 模板参数: 若无模板参数，则设置为空*//*

            String[] templateParams = {"5678"};
            req.setTemplateParamSet(templateParams);

            */
/* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
 * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 *//*

            SendSmsResponse res = client.SendSms(req);

            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(res));

            // 也可以取出单个值，你可以通过官网接口文档或跳转到response对象的定义处查看返回字段的定义
            System.out.println(res.getRequestId());


            DescribeInstancesRequest req = new DescribeInstancesRequest();

            DescribeInstancesResponse resp = client.DescribeInstances(req);

            System.out.println(DescribeInstancesResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }
}
*/
