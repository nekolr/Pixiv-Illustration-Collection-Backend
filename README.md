<p align="center">
<img src="https://pixivic.com/img/icon.9a42bbfa.svg" width = "129" height = "90" alt="pixivic_icon"/>
</p>

 ## 前言

## About Code

### 架构图

![](https://s2.ax1x.com/2020/03/10/8Fn2kT.png)

### Bright Point

- 基于注解的基础设施模块
- 基于`ElasticSearch`的近实时搜索
- 基于`redis`的`stream`作为消息队列构成消息模块
- 基于`Logstash`的同步
- 基于`JDK 11`的`Httpclient`
- `Restful` API

## About Feature

### 检索

- 根据画师id检索画师
- 根据画作id检索画作详情
- 依赖于`saucenao`的以图搜图
- 依赖于`Pixiv`的相关性tag
- 依赖于`Pixiv`的搜索词自动补全
- 依赖于百科的搜索词->词条的尝试（搜索建议）
- 多关键词搜索（空格切分）
- 自动翻译
- 更高的定制性（长宽、画作类型、创建时间等）

### 排行

- 日、周、月(各种模式)排行的查看

### spotlight

- spotlight主题画辑的查看

### ACG资讯

- 集合资讯查看

### Common

- 用户模块

- 收藏画作

- 获取画作关联画作列表

- follow画师

- 关注画师最新画作

- 查看画师画作(及其汇总信息)

- 给画作添加Tag

- 评论及点赞模块

- 通用消息模块

- 用户行为追踪与还原(时间轴)、支持自定义书签(备份点)(暂未实现)

- 喜爱画作分享到社群(暂未实现)

  