package dev.cheerfun.pixivic.biz.web.cibr.service;

import org.springframework.stereotype.Service;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/11/30 9:28 PM
 * @description DeepDanbooruService
 */
@Service
public class DeepDanbooruService {

    //接受图片，缩放为512，512，转为矩阵后请求tf serving
    //得到结果后过滤出0.6分以上的index，从数据库中查找对应的label，优先列出角色标签，之后显示有对应pixivtab的标签
    //用户在页面上可以点击tag来搜索

}
