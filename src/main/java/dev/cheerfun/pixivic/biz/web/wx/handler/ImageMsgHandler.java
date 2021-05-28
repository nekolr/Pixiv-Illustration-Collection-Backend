package dev.cheerfun.pixivic.biz.web.wx.handler;

import dev.cheerfun.pixivic.biz.web.wx.builder.TextBuilder;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
//@Component
public class ImageMsgHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {
        //文字 自动回复
        //图片，进行以图搜图
        System.out.println(wxMessage);
        //TODO 组装回复消息
        String content = "收到信息内容：" + (wxMessage);
        WxMpXmlOutMessage.TEXT()
                .content("content")
                .fromUser("to")
                .toUser("from")
                .build();
        return new TextBuilder().build(content, wxMessage, weixinService);

    }

}
