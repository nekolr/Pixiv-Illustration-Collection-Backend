<p align="center">
<img src="https://pixivic.com/img/icon.9a42bbfa.svg" width = "129" height = "90" alt="pixivic_icon"/>
</p>


 ## 简介
 
 #### 她是谁
 
 Pixivic.com目前是一个以瀑布流为主要展示方式的pixiv画作搜索平台，她并不存储画作图片本身，仅仅是依赖于画作元数据对画作进行处理，提供类似热门搜索、个人推荐、相关画作、历史记录等功能。基于这些工具功能基础上，她也提供了周边用户相关的功能，例如收藏、关注、评论。
 
 #### 她的外表
 
 APP：
 
 ![mi head](https://cdn.jsdelivr.net/gh/OysterQAQ/Blog-Image/mi%20head.png)
 
 PC：
 
 ![截屏2020-07-16 下午4.21.36](https://cdn.jsdelivr.net/gh/OysterQAQ/Blog-Image/%E6%88%AA%E5%B1%8F2020-07-16%20%E4%B8%8B%E5%8D%884.21.36.png)
 
 

## 技术

### 架构图

![](https://s1.ax1x.com/2020/08/26/dRdNPH.png)

### Bright Point

- 基于注解的基础设施aop模块（jwt权限校验、令牌桶限流、状态机敏感词过滤、验证码校验）
- 基于[Deeplearning4j](https://github.com/eclipse/deeplearning4j)使用机器学习训练的nsfw模型进行图片色情检测
- 基于[Graphicsmagick](http://www.graphicsmagick.org/)的图片裁剪
- 基于[ElasticSearch](https://www.elastic.co/elasticsearch/)的近实时搜索
- 基于[Rabbitmq](https://www.rabbitmq.com/)作为消息总线构成事件驱动的业务模型
- 基于[Logstash](https://www.elastic.co/logstash)的同步
- 基于[Mahout](https://mahout.apache.org/)的简单单机离线推荐
- 基于`JDK 11`的`Httpclient`
- `Restful` API

#### Api Doc

[html](https://github.com/OysterQAQ/Pixiv-Illustration-Collection-Backend/blob/master/Pixivic.com%20Api%20Doc.html)

[md](https://github.com/OysterQAQ/Pixiv-Illustration-Collection-Backend/blob/master/Pixivic.com%20Api%20Doc.md)