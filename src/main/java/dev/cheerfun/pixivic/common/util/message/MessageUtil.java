package dev.cheerfun.pixivic.common.util.message;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import dev.cheerfun.pixivic.common.po.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/27 10:01 AM
 * @description MessageUtil
 */
@Component
@Slf4j
public class MessageUtil {
    private LinkedBlockingQueue<Message> waitForSendQueue;
    @Autowired
    private ExecutorService messageExecutorService;
    private SmsClient smsClient;
    @Value("${messageApi.appid}")
    private String appid;
    @Value("${messageApi.templateID}")
    private String templateID;
    @Value("${messageApi.sign}")
    private String sign;
    @Value("${messageApi.secretId}")
    private String secretId;
    @Value("${messageApi.secretKey}")
    private String secretKey;
    @Value("${messageApi.endpoint}")
    private String endpoint;

    @PostConstruct
    public void init() {
        try {
            log.info("开始初始化短信发送工具类");
            Credential cred = new Credential(secretId, secretKey);
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
            httpProfile.setEndpoint(endpoint);
            /* 非必要步骤:
             * 实例化一个客户端配置对象，可以指定超时时间等配置 */
            ClientProfile clientProfile = new ClientProfile();
            /* SDK默认用TC3-HMAC-SHA256进行签名
             * 非必要请不要修改这个字段 */
            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);
            /* 实例化要请求产品(以sms为例)的client对象
             * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，或者引用预设的常量 */
            smsClient = new SmsClient(cred, "", clientProfile);
            waitForSendQueue = new LinkedBlockingQueue<>(1000 * 1000 * 1000);
            dealWaitForSendQueue();
        } catch (Exception e) {
            log.error("初始化短信发送工具类失败");
            e.printStackTrace();
        }
        log.info("初始化短信发送工具类成功");

    }

    public void sendMessageAsync(Message message) {
        if (waitForSendQueue.offer(message)) {
            log.info("发送给" + message.getPhone() + "的短信进入发送队列");
        } else {
            log.error("发送给" + message.getPhone() + "邮件进入发送队列失败，队列已满");
        }
    }

    public void sendMessage(Message message) throws TencentCloudSDKException {
        SendSmsRequest req = new SendSmsRequest();
        req.setSmsSdkAppid(appid);
        req.setSign(sign);
        req.setTemplateID(templateID);
        String[] phoneNumbers = {"+86" + message.getPhone()};
        req.setPhoneNumberSet(phoneNumbers);
        String[] templateParams = {message.getContent()};
        req.setTemplateParamSet(templateParams);
        SendSmsResponse res = smsClient.SendSms(req);
    }

    public void dealWaitForSendQueue() {
        Runnable consumer = () -> {
            while (true) {
                Message message = null;
                try {
                    message = waitForSendQueue.take();
                    sendMessage(message);
                    log.info("发送给" + message.getPhone() + "短信发送成功");
                } catch (Exception e) {
                    log.error("发送给" + message.getPhone() + "短信发送失败");
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < 5; i++) {
            messageExecutorService.submit(consumer);
        }
    }
}
