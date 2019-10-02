## 前言

## About Code

### 架构图

![](https://upload.cc/i1/2019/09/21/LC8gfm.png)

### Bright Point

- 基于注解的基础设施模块
- 基于`ElasticSearch`的近实时搜索
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

- 日、周、月排行的查看

### spotlight

- spotlight主题画辑的查看

### Common

- 用户模块
- 收藏画作
- follow画师
- 查看画师画作
- 给画作添加Tag
- 用户行为追踪与还原(时间轴)、支持自定义书签(备份点)
- 喜爱画作分享到社群



