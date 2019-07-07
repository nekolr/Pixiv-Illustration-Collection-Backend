# pixivic项目web后端
## 技术选型(待补充)
技术栈 | 作用
---|---
spring5 | spring容器，管理Bean
springboot2.1.5.RELEASE | 快速构建web环境，便捷开发，内部提供SPI机制
mybatis-plus1.0.5 | 内含乐观锁插件、代码生成器，mybatis增强版快速开发
springdata | 提供redis、mongo等模版方法插件开发便捷
mysql数据库 | 熟悉，轻量，问题易搜索
fastjson | 使用简单
lombok | 快速开发
jjwt | json token web 轻量便捷简单
待续.... | 待续...

## 功能模块
- [ ] 用户中心
- [ ] 会员模块
- [ ] 待补充...

## 项目结构
```$xslt
.
├── README.md --- 工程介绍
├── docs
│   ├── readme.md --- 更新记录
│   └── sql --- 数据库脚本
├── pom.xml
├── api -- 对外提供api，服务配置dubbo
│   ├── pom.xml
│   ├── src
├── biz -- 服务实现层
│   ├── pom.xml
│   ├── src
├── deploy -- 发布容器
│   ├── pom.xml
│   ├── src
├── domain -- 领域模型
│   ├── src
│   ├── pom.xml
├── infrastructure -- 第三方依赖工具层
│   ├── pom.xml
│   ├── src
├── web -- 前端服务接入层
│   ├── pom.xml
│   ├── src
└── archetype.iml

```
```$xslt
.
├── README.md
├── pixiv-Illustration-collection-web.iml
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── dev
│   │   │       └── cheerfun
│   │   │           └── pixivic
│   │   │               ├── Application.java
│   │   │               └── web
│   │   │                   ├── config
│   │   │                   │   └── MybatisPlusConfig.java
│   │   │                   ├── controller
│   │   │                   │   ├── IllustsArtistController.java
│   │   │                   │   ├── IllustsArtistProfileController.java
│   │   │                   │   ├── IllustsController.java
│   │   │                   │   ├── IllustsImageUrlsController.java
│   │   │                   │   └── IllustsTagsController.java
│   │   │                   ├── entity
│   │   │                   │   ├── Illusts.java
│   │   │                   │   ├── IllustsArtist.java
│   │   │                   │   ├── IllustsArtistProfile.java
│   │   │                   │   ├── IllustsImageUrls.java
│   │   │                   │   └── IllustsTags.java
│   │   │                   ├── mapper
│   │   │                   │   ├── IllustsArtistMapper.java
│   │   │                   │   ├── IllustsArtistProfileMapper.java
│   │   │                   │   ├── IllustsImageUrlsMapper.java
│   │   │                   │   ├── IllustsMapper.java
│   │   │                   │   └── IllustsTagsMapper.java
│   │   │                   └── service
│   │   │                       ├── IIllustsArtistProfileService.java
│   │   │                       ├── IIllustsArtistService.java
│   │   │                       ├── IIllustsImageUrlsService.java
│   │   │                       ├── IIllustsService.java
│   │   │                       ├── IIllustsTagsService.java
│   │   │                       └── impl
│   │   │                           ├── IllustsArtistProfileServiceImpl.java
│   │   │                           ├── IllustsArtistServiceImpl.java
│   │   │                           ├── IllustsImageUrlsServiceImpl.java
│   │   │                           ├── IllustsServiceImpl.java
│   │   │                           └── IllustsTagsServiceImpl.java
│   │   └── resources
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── application.yml
│   │       ├── logback-dev.xml
│   │       ├── logback-prod.xml
│   │       └── mapper
│   │           ├── IllustsArtistMapper.xml
│   │           ├── IllustsArtistProfileMapper.xml
│   │           ├── IllustsImageUrlsMapper.xml
│   │           ├── IllustsMapper.xml
│   │           └── IllustsTagsMapper.xml
│   └── test
│       └── java
│           └── dev
│               └── cheerfun
│                   └── pixivic
│                       ├── RunTest.java
│                       └── code
│                           └── CodeGenerator.java
└── target
    ├── classes
    │   ├── META-INF
    │   │   └── pixiv-Illustration-collection-web.kotlin_module
    │   ├── application-dev.yml
    │   ├── application-prod.yml
    │   ├── application.yml
    │   ├── dev
    │   │   └── cheerfun
    │   │       └── pixivic
    │   │           ├── Application.class
    │   │           └── web
    │   │               ├── config
    │   │               │   └── MybatisPlusConfig.class
    │   │               ├── controller
    │   │               │   ├── IllustsArtistController.class
    │   │               │   ├── IllustsArtistProfileController.class
    │   │               │   ├── IllustsController.class
    │   │               │   ├── IllustsImageUrlsController.class
    │   │               │   └── IllustsTagsController.class
    │   │               ├── entity
    │   │               │   ├── Illusts.class
    │   │               │   ├── IllustsArtist.class
    │   │               │   ├── IllustsArtistProfile.class
    │   │               │   ├── IllustsImageUrls.class
    │   │               │   └── IllustsTags.class
    │   │               ├── mapper
    │   │               │   ├── IllustsArtistMapper.class
    │   │               │   ├── IllustsArtistProfileMapper.class
    │   │               │   ├── IllustsImageUrlsMapper.class
    │   │               │   ├── IllustsMapper.class
    │   │               │   └── IllustsTagsMapper.class
    │   │               └── service
    │   │                   ├── IIllustsArtistProfileService.class
    │   │                   ├── IIllustsArtistService.class
    │   │                   ├── IIllustsImageUrlsService.class
    │   │                   ├── IIllustsService.class
    │   │                   ├── IIllustsTagsService.class
    │   │                   └── impl
    │   │                       ├── IllustsArtistProfileServiceImpl.class
    │   │                       ├── IllustsArtistServiceImpl.class
    │   │                       ├── IllustsImageUrlsServiceImpl.class
    │   │                       ├── IllustsServiceImpl.class
    │   │                       └── IllustsTagsServiceImpl.class
    │   ├── logback-dev.xml
    │   ├── logback-prod.xml
    │   └── mapper
    │       ├── IllustsArtistMapper.xml
    │       ├── IllustsArtistProfileMapper.xml
    │       ├── IllustsImageUrlsMapper.xml
    │       ├── IllustsMapper.xml
    │       └── IllustsTagsMapper.xml
    ├── generated-sources
    │   └── annotations
    ├── generated-test-sources
    │   └── test-annotations
    └── test-classes
        └── dev
            └── cheerfun
                └── pixivic
                    ├── RunTest.class
                    └── code
                        ├── CodeGenerator$1.class
                        ├── CodeGenerator$2.class
                        └── CodeGenerator.class

```

## 资料
* [mybatis-plus官方文档](https://mp.baomidou.com/guide/tenant.html)
* [jjwt](https://www.jianshu.com/p/d215e70dc1f9)
* [简易功能思维导图](http://note.youdao.com/noteshare?id=0389ef94e6ad21dd0e983ac9ef1e10e7)

## 开发规范
[git开发规范](https://juejin.im/post/5b4328bbf265da0fa21a6820)

