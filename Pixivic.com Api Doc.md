##  Pixivic.com Api Doc

### 搜索相关

#### 1\. 搜索画作

###### 接口功能
> 根据关键词搜索画作


###### URL
> https://api.pixivic.com/illustrations


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> | 参数名     | 必选 | 类型   | 说明                                    |
> | :--------- | :--- | :----- | --------------------------------------- |
> | keyword    | 是   | string | 搜索关键词                              |
> | pageSize   | 是   | int    | 页大小                                  |
> | page       | 是   | int    | 页数（1开始）                           |
> | searchType | 否   | string | 搜索类型（可选original和autoTranslate） |
> | illustType | 否   | string | 画作类型（可选manga和illust）           |
> | minWidth   | 否   | int    | 最小宽度                                |
> | minHeight  | 否   | int    | 最小高度                                |
> | beginDate  | 否   | string | 画作发布日期限制                        |
> | endDate    | 否   | string | 画作发布日期限制                        |
> | xRestrict  | 否   | int    | R18开关                                 |

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：[https://api.pixivic.com/illustrations?keyword=アルトリア・ペンドラゴン&page=1&pageSize=1&searchType=original&illustType=manga&minWidth=800&minHeight=800&beginDate=2019-01-01&endDate=9999-12-31&xRestrict=0]()  
> 获取成功(200)
``` javascript
{
    "message": "搜索结果获取成功",
    "data": [
        {
            "id": 77080424,
            "artistId": 46393,
            "title": "カルディアんバスターズ",
            "type": "manga",
            "caption": "うぃーなーうぃーなーちきんでぃなー<br />今日のご飯はドン勝つだ！！",
            "artistPreView": {
                "id": 46393,
                "name": "小雨大豆☆学生夫婦１巻発売中！",
                "account": "kosamedaizu",
                "avatar": "https://i.pximg.net/user-profile/img/2011/01/04/15/48/37/2584657_e5029f6ca590e20f62f19e94696971fa_170.jpg"
            },
            "tags": [
                {
                    "id": 1308,
                    "name": "漫画",
                    "translatedName": "manga"
                },
                {
                    "id": 7793478,
                    "name": "葛飾北斎(Fate)",
                    "translatedName": "葛饰北斋(Fate)"
                },
                {
                    "id": 3142476,
                    "name": "FGO",
                    "translatedName": "Fate/Grand Order"
                },
                {
                    "id": 2143526,
                    "name": "アルトリア・ペンドラゴン",
                    "translatedName": "阿尔托利亚·潘德拉贡"
                },
                {
                    "id": 70366303,
                    "name": "恋愛クソ雑魚水着剣豪",
                    "translatedName": ""
                },
                {
                    "id": 2047449,
                    "name": "Fate/GrandOrder",
                    "translatedName": "Fate/Grand Order"
                },
                {
                    "id": 4772743,
                    "name": "Fate/GO1000users入り",
                    "translatedName": "Fate/GO1000users加入书籤"
                }
            ],
            "imageUrls": [
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/10/03/00/00/09/77080424_p0_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/10/03/00/00/09/77080424_p0_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/10/03/00/00/09/77080424_p0_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/10/03/00/00/09/77080424_p0.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/10/03/00/00/09/77080424_p1_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/10/03/00/00/09/77080424_p1_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/10/03/00/00/09/77080424_p1_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/10/03/00/00/09/77080424_p1.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/10/03/00/00/09/77080424_p2_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/10/03/00/00/09/77080424_p2_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/10/03/00/00/09/77080424_p2_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/10/03/00/00/09/77080424_p2.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/10/03/00/00/09/77080424_p3_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/10/03/00/00/09/77080424_p3_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/10/03/00/00/09/77080424_p3_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/10/03/00/00/09/77080424_p3.jpg"
                }
            ],
            "tools": [
                "SAI",
                "Photoshop"
            ],
            "createDate": 1569999609000,
            "pageCount": 4,
            "width": 1000,
            "height": 1542,
            "sanityLevel": 2,
            "restrict": 0,
            "totalView": 57769,
            "totalBookmarks": 1650,
            "xrestrict": 0
        }
    ]
}
```



#### 2\. 以图搜图

###### 接口功能
> 根据图片url检索原画作地址


###### URL
> https://api.pixivic.com/similarityImages


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> | 参数名   | 必选 | 类型   | 说明    |
> | :------- | :--- | :----- | ------- |
> | imageUrl | 是   | string | 图片URL |

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/similarityImages?imageUrl=https://i0.wp.com/i.ibb.co/yBmJDzR/27-0.jpg 
> 获取成功(200)
``` javascript
{
    "message": "搜索结果获取成功",
    "data": {
        "results": [
            {
                "header": {
                    "similarity": "97.50",
                    "thumbnail": "https://img1.saucenao.com/res/pixiv/7685/76858511_p0_master1200.jpg?auth=I2JdzvS153LmqJL8U7Gxzg&exp=1572147796"
                },
                "data": {
                    "originalUrls": [
                        "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=76858511"
                    ],
                    "title": "무제",
                    "illustId": "76858511",
                    "artistName": "ファジョボレ",
                    "artistId": "16183476"
                }
            },
            {
                "header": {
                    "similarity": "95.65",
                    "thumbnail": "https://img3.saucenao.com/booru/4/f/4fb4415d2fe8fde21de830ddbe421f4f_1.jpg"
                },
                "data": {
                    "originalUrls": [
                        "https://yande.re/post/show/571470"
                    ],
                    "title": null,
                    "illustId": null,
                    "artistName": null,
                    "artistId": null
                }
            },
            {
                "header": {
                    "similarity": "95.28",
                    "thumbnail": "https://img3.saucenao.com/booru/8/0/80217664b5ce300e98706aa83dc8370e_2.jpg"
                },
                "data": {
                    "originalUrls": [
                        "https://danbooru.donmai.us/post/show/3641244",
                        "https://gelbooru.com/index.php?page=post&s=view&id=4933457"
                    ],
                    "title": null,
                    "illustId": null,
                    "artistName": null,
                    "artistId": null
                }
            }
        ]
    }
}
```

 

#### 3\. 获取搜索建议

###### 接口功能
> 获取该搜索词的专属词条（往往是一些词汇所对应的acg作品名称，搜索词注意url编码）


###### URL
> https://api.pixivic.com/keywords/{keyword}/suggestions


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/keywords/黄漫老师/suggestions  
> 获取成功(200)
``` javascript
{
    "message": "搜索建议获取成功",
    "data": [
        {
            "keyword": "エロマンガ先生",
            "keywordTranslated": "埃罗芒阿老师"
        },
        {
            "keyword": "エロマンガ先生",
            "keywordTranslated": "埃罗芒阿老师"
        },
        {
            "keyword": "エロマンガ先生",
            "keywordTranslated": "埃罗芒阿老师"
        }
    ]
}
```

 

#### 4\. 获取搜索候选词

###### 接口功能
> 获取关键词候选词，搜索关键词补全（注意url编码）


###### URL
> https://api.pixivic.com/keywords/{keyword}/candidates


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/keywords/我的/candidates 
> 获取成功(200)
``` javascript
{
    "message": "搜索候选词获取成功",
    "data": {
        "keywordList": [
            "我的英雄学院",
            "我的妹妹不可能那么可爱",
            "我的愛人"
        ]
    }
}
```

 

#### 5\. 获取来自pixiv的搜索建议

###### 接口功能
> 获取该搜索词相关联的tag


###### URL
> https://api.pixivic.com/keywords/{keyword}/pixivSuggestions


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/keywords/saber/pixivSuggestions  
> 获取成功(200)
``` javascript
{
    "message": "搜索建议(来自Pixiv)获取成功",
    "data": [
        {
            "keyword": "FGO",
            "keywordTranslated": ""
        },
        {
            "keyword": "Fate/GrandOrder",
            "keywordTranslated": ""
        },
        {
            "keyword": "Fate",
            "keywordTranslated": ""
        },
        {
            "keyword": "アルトリア・ペンドラゴン",
            "keywordTranslated": "阿尔托利亚·潘德拉贡"
        },
        {
            "keyword": "fate",
            "keywordTranslated": ""
        },
        {
            "keyword": "saber",
            "keywordTranslated": ""
        },
        {
            "keyword": "セイバーウォーズ2〜始まりの宇宙へ〜",
            "keywordTranslated": ""
        },
        {
            "keyword": "Saber",
            "keywordTranslated": ""
        }
    ]
}
```



 

#### 6\. 翻译搜索词

###### 接口功能
> 获取该搜索词日语翻译


###### URL
> https://api.pixivic.com/keywords/{keyword}/translations


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/keywords/进击的巨人/translations  
> 获取成功(200)
``` javascript
{
    "message": "搜索词翻译获取成功",
    "data": {
        "keyword": "進撃の巨人",
        "keywordTranslated": "进击的巨人"
    }
}
```



### 排行相关

#### 1\. 获取排行列表

###### 接口功能
> 获取指定排行列表


###### URL
> https://api.pixivic.com/ranks


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> | 参数名   | 必选 | 类型   | 说明                   |
> | :------- | :--- | :----- | ---------------------- |
> | date     | 是   | String | 日期                   |
> | mode     | 是   | String | 模式（day/week/month） |
> | page     | 是   | int    | 页数                   |
> | pageSize | 否   | int    | 页面大小               |

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/ranks?page=1&date=2019-10-10&mode=week&page=2&pageSize=1获取成功(200)
``` javascript
{
  "message": "获取排行成功",
  "data": {
    "data": [
      {
        "id": 77119609,
        "artistId": 14499092,
        "title": "こたつから出れなくなった雪女。",
        "type": "illust",
        "caption": "【FANBOX】pixiv.net/fanbox/creator/14499092<br />【Fantia】fantia.jp/fanclubs/17120",
        "artistPreView": {
          "id": 14499092,
          "name": "ばかやろうコミティアれ-36a",
          "account": "araidokagiri",
          "avatar": "https://i.pximg.net/user-profile/img/2017/01/04/22/25/20/11960674_3e0a1843088654ad0945b30129081d59_170.png"
        },
        "tags": [
          {
            "id": null,
            "name": "雪女",
            "translatedName": "yuki-onna"
          },
          {
            "id": null,
            "name": "オリジナル",
            "translatedName": "原创"
          },
          {
            "id": null,
            "name": "四角いブラックホール",
            "translatedName": ""
          },
          {
            "id": null,
            "name": "ほぼぬらりひょん",
            "translatedName": ""
          },
          {
            "id": null,
            "name": "アイデンティティの放棄",
            "translatedName": ""
          },
          {
            "id": null,
            "name": "こたつむり",
            "translatedName": ""
          },
          {
            "id": null,
            "name": "妖怪ごくつぶし",
            "translatedName": ""
          },
          {
            "id": null,
            "name": "畳化",
            "translatedName": ""
          },
          {
            "id": null,
            "name": "妖怪食っちゃ寝",
            "translatedName": ""
          },
          {
            "id": null,
            "name": "貴方の為に冷やしておきましたよ",
            "translatedName": ""
          }
        ],
        "imageUrls": [
          {
            "squareMedium": "https://i.pximg.net/c/540x540_10_webp/img-master/img/2019/10/05/12/37/27/77119609_p0_square1200.jpg",
            "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/10/05/12/37/27/77119609_p0_master1200.jpg",
            "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/10/05/12/37/27/77119609_p0_master1200.jpg",
            "original": "https://i.pximg.net/img-original/img/2019/10/05/12/37/27/77119609_p0.jpg"
          }
        ],
        "tools": [],
        "createDate": 1570246647000,
        "pageCount": 1,
        "width": 1587,
        "height": 2207,
        "sanityLevel": 2,
        "restrict": 0,
        "totalView": 63154,
        "totalBookmarks": 5723,
        "xrestrict": 0
      }
    ],
    "mode": "week",
    "date": "2019-10-10"
  }
}
```

### Spotlights相关

 

#### 1\. 获取spotlights列表

###### 接口功能
> 获取spotlights列表


###### URL
> https://api.pixivic.com/spotlights


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> | 参数名   | 必选 | 类型 | 说明 |
> | :------- | :--- | :--- | ---- |
> | page     | 是   | int  |      |
> | pageSize | 是   | int  |      |

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/spotlights?page=1&pageSize=2 
> 获取成功(200)
``` javascript
{
    "message": "获取Spotlight列表成功",
    "data": [
        {
            "id": 1,
            "title": "斗争吧！动物系列精选",
            "pureTitle": "斗争吧！动物系列精选",
            "thumbnail": "https://i-ogp.pximg.net/c/1050x550_80_a2_g2/img-master/img/2011/05/08/22/45/27/18750821_p0_master1200.jpg",
            "articleUrl": "https://www.pixivision.net/zh/a/1",
            "publishDate": 1402185600000,
            "category": "spotlight",
            "subcategoryLabel": "插画"
        },
        {
            "id": 2,
            "title": "【阅览注意】让你不由自主流口水的那些家伙...【食物恐怖分子】 　",
            "pureTitle": "【阅览注意】让你不由自主流口水的那些家伙...【食物恐怖分子】 　",
            "thumbnail": "https://i-ogp.pximg.net/c/1050x550_80_a2_g2/img-master/img/2013/09/07/00/38/48/38340283_p0_master1200.jpg",
            "articleUrl": "https://www.pixivision.net/zh/a/2",
            "publishDate": 1402272000000,
            "category": "spotlight",
            "subcategoryLabel": "插画"
        }
    ]
}
```

#### 2\. 获取spotlights详细

###### 接口功能
> 获取spotlights详细


###### URL
> https://api.pixivic.com/spotlights/{spotlightId} 


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/spotlights/2
> 获取成功(200)
``` javascript
{
    "message": "获取Spotlight详情成功",
    "data": {
        "id": 2,
        "title": "【阅览注意】让你不由自主流口水的那些家伙...【食物恐怖分子】 　",
        "pureTitle": "【阅览注意】让你不由自主流口水的那些家伙...【食物恐怖分子】 　",
        "thumbnail": "https://i-ogp.pximg.net/c/1050x550_80_a2_g2/img-master/img/2013/09/07/00/38/48/38340283_p0_master1200.jpg",
        "articleUrl": "https://www.pixivision.net/zh/a/2",
        "publishDate": 1402272000000,
        "category": "spotlight",
        "subcategoryLabel": "插画"
    }
}
```

#### 3\. 获取spotlights下画作列表

###### 接口功能
> 获取spotlights列表


###### URL
> https://api.pixivic.com/spotlights/{spotlightId}/illustrations


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/spotlights?page=1&pageSize=2 
> 获取成功(200)
``` javascript
{
    "message": "获取该spotlight下画作列表成功",
    "data": [
        {
            "id": 1788244,
            "artistId": 352490,
            "title": "ポケモン描けるかな",
            "type": "illust",
            "caption": "麦茶と竜の合作<br />初投稿",
            "artistPreView": {
                "id": 352490,
                "name": "麦茶",
                "account": "mu-g1a",
                "avatar": "https://i.pximg.net/user-profile/img/2012/08/26/21/22/35/5066079_976430246f0b974612eda9341ad12367_170.jpg"
            },
            "tags": [
                {
                    "id": 1200,
                    "name": "ポケモン",
                    "translatedName": "精灵宝可梦"
                },
                {
                    "id": 71394947,
                    "name": "ポケモン描けるかな",
                    "translatedName": ""
                }
            ],
            "imageUrls": [
                {
                    "squareMedium": "https://i.pximg.net/c/540x540_10_webp/img-master/img/2008/10/05/18/41/44/1788244_p0_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2008/10/05/18/41/44/1788244_p0_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2008/10/05/18/41/44/1788244_p0_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2008/10/05/18/41/44/1788244_p0.jpg"
                }
            ],
            "tools": [
                "シャープペンシル"
            ],
            "createDate": 1223199704000,
            "pageCount": 1,
            "width": 2508,
            "height": 3356,
            "sanityLevel": 2,
            "restrict": 0,
            "totalView": 392,
            "totalBookmarks": 0,
            "xrestrict": 0
        },
        {
            "id": 3160026,
            "artistId": 426744,
            "title": "しゃお",
            "type": "illust",
            "caption": "また描いてみました～♪難しいところや面倒な<br />ところは描いてませんｗ模様とか適当だよ",
            "artistPreView": {
                "id": 426744,
                "name": "カーノ",
                "account": "white-kano",
                "avatar": "https://i.pximg.net/user-profile/img/2013/05/10/22/38/13/6216652_02396f99e8dec9457c44bf154d14e4dd_170.jpg"
            },
            "tags": [
                {
                    "id": 747,
                    "name": "ケモノ",
                    "translatedName": "野兽"
                },
                {
                    "id": 11085,
                    "name": "竜",
                    "translatedName": "chinese dragon"
                },
                {
                    "id": 280712,
                    "name": "シャオルーン",
                    "translatedName": "Shaorune"
                }
            ],
            "imageUrls": [
                {
                    "squareMedium": "https://i.pximg.net/c/540x540_10_webp/img-master/img/2009/02/19/20/47/42/3160026_p0_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2009/02/19/20/47/42/3160026_p0_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2009/02/19/20/47/42/3160026_p0_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2009/02/19/20/47/42/3160026_p0.jpg"
                }
            ],
            "tools": [
                "SAI"
            ],
            "createDate": 1235044062000,
            "pageCount": 1,
            "width": 675,
            "height": 917,
            "sanityLevel": 2,
            "restrict": 0,
            "totalView": 989,
            "totalBookmarks": 4,
            "xrestrict": 0
        },
        {
            "id": 17110170,
            "artistId": 342408,
            "title": "途中経過",
            "type": "illust",
            "caption": "ポヌぴンさんからのリクエストでキャッツフェアリー……、の途中です。尻尾フェチとの事で、どういう風に尻尾を表現すればその魅力を引き出せるのか？　ライン？　置き方？　何かへ絡めること？　様々な疑問をはらみながら、一つの思考の答えとして猫っぽさを強調したキャッツフェアリーの構図で筆をとってみました。　しかし、ここで一つ新たな提示がなされます。そう、「尻尾攻めとか好みです」と。　……ほほう、そうきましたか。常日頃からネタ絵師としてやっているつもりでしたが、最近のリク等の傾向からもしや、いや真逆……。等と思っていましたが、どうやら私に対してエロ絵師としての認識がある様だと。絵師として生を受けたのならば、期待に応えるのが人の常。やりますやれます、やってみせます！！　んな訳でこれをボツって、新しくエロいのを仕上げにかかるでござる。あと、そろそろ修理に出したパソが届きそうなので、もちっとばかり待っててくださいｗ",
            "artistPreView": {
                "id": 342408,
                "name": "山中くじら",
                "account": "kujirayamanaka",
                "avatar": "https://i.pximg.net/user-profile/img/2010/08/11/23/25/34/2075355_662cce233da9280d5f9f7c551f779935_170.jpg"
            },
            "tags": [
                {
                    "id": 5544,
                    "name": "遊戯王",
                    "translatedName": "游戏王"
                },
                {
                    "id": 58285726,
                    "name": "キャッツ・フェアリー",
                    "translatedName": ""
                }
            ],
            "imageUrls": [
                {
                    "squareMedium": "https://i.pximg.net/c/540x540_10_webp/img-master/img/2011/03/04/04/47/01/17110170_p0_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2011/03/04/04/47/01/17110170_p0_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2011/03/04/04/47/01/17110170_p0_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2011/03/04/04/47/01/17110170_p0.jpg"
                }
            ],
            "tools": [
                "Pixia"
            ],
            "createDate": 1299181621000,
            "pageCount": 1,
            "width": 900,
            "height": 1000,
            "sanityLevel": 4,
            "restrict": 0,
            "totalView": 785,
            "totalBookmarks": 3,
            "xrestrict": 0
        },
        {
            "id": 21751422,
            "artistId": 3163290,
            "title": "らくがき",
            "type": "illust",
            "caption": "あふん",
            "artistPreView": {
                "id": 3163290,
                "name": "ぬこ蓄＠no man",
                "account": "517satiare",
                "avatar": "https://i.pximg.net/user-profile/img/2015/02/07/21/21/41/8941259_c8f2ca3051b86a94add83dc96f0dea3d_170.png"
            },
            "tags": [
                {
                    "id": 849,
                    "name": "落書き",
                    "translatedName": "涂鸦"
                },
                {
                    "id": 41,
                    "name": "オリジナル",
                    "translatedName": "原创"
                }
            ],
            "imageUrls": [
                {
                    "squareMedium": "https://i.pximg.net/c/540x540_10_webp/img-master/img/2011/09/14/23/38/09/21751422_p0_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2011/09/14/23/38/09/21751422_p0_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2011/09/14/23/38/09/21751422_p0_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2011/09/14/23/38/09/21751422_p0.jpg"
                }
            ],
            "tools": [
                "ボールペン"
            ],
            "createDate": 1316011089000,
            "pageCount": 1,
            "width": 240,
            "height": 400,
            "sanityLevel": 4,
            "restrict": 0,
            "totalView": 147,
            "totalBookmarks": 0,
            "xrestrict": 0
        },
        {
            "id": 31731664,
            "artistId": 1908608,
            "title": "融合式ぬいぐるみ化",
            "type": "illust",
            "caption": "ども！今度は、ぬいぐるみ化に手を染めてみます。といっても、ぬいぐるみ化タグのついた作品は、いずれも傑作、秀作、良作ばかり。多少は自分色を出したいなあ。ということで人間の頭身を残したままでのぬいぐるみ化を考えてみたけど、これだと着ぐるみとの差別化が図りづらい。そこで、頭身は人間に近いけど、あちこちが微妙に人間のプロポーションではないぬいぐるみ化という前提にデザインしてみました。それと顔の方も人間の原型と面影をを残した上で、ぬいぐるみあるいは動物の特徴を組み入れるって感じで。しかし、球体関節人形化といい、オイラは、「人に似てしかし人にあらざるモノ」に萌える性癖であることを改めて再確認。",
            "artistPreView": {
                "id": 1908608,
                "name": "ほだれ酒",
                "account": "hodarezake",
                "avatar": "https://i.pximg.net/user-profile/img/2010/06/19/09/20/02/1880342_ca5054dd849ac9b1b4157397c18f56a7_170.jpg"
            },
            "tags": [
                {
                    "id": 205878,
                    "name": "ぬいぐるみ化",
                    "translatedName": "plushie transformation"
                },
                {
                    "id": 569538,
                    "name": "融合",
                    "translatedName": "agglutination"
                },
                {
                    "id": 37294437,
                    "name": "ケモぬい",
                    "translatedName": ""
                },
                {
                    "id": 5937,
                    "name": "状態変化",
                    "translatedName": "状态变化"
                },
                {
                    "id": 146909,
                    "name": "合体",
                    "translatedName": "combination"
                },
                {
                    "id": 849,
                    "name": "落書き",
                    "translatedName": "涂鸦"
                },
                {
                    "id": 473859,
                    "name": "transfur",
                    "translatedName": ""
                },
                {
                    "id": 1492948,
                    "name": "擬物化",
                    "translatedName": ""
                },
                {
                    "id": 1494428,
                    "name": "物品化",
                    "translatedName": "object transformation"
                }
            ],
            "imageUrls": [
                {
                    "squareMedium": "https://i.pximg.net/c/540x540_10_webp/img-master/img/2012/11/25/18/17/10/31731664_p0_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2012/11/25/18/17/10/31731664_p0_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2012/11/25/18/17/10/31731664_p0_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2012/11/25/18/17/10/31731664_p0.jpg"
                }
            ],
            "tools": [
                "Photoshop",
                "シャープペンシル"
            ],
            "createDate": 1353835030000,
            "pageCount": 1,
            "width": 615,
            "height": 1024,
            "sanityLevel": 2,
            "restrict": 0,
            "totalView": 4575,
            "totalBookmarks": 40,
            "xrestrict": 0
        }
    ]
}
```



### ACG资讯相关

#### 1\. 获取资讯列表

###### 接口功能
> 获取资讯列表


###### URL
> https://api.pixivic.com/news/{referer}


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> | 参数名 | 必选 | 类型   | 说明 |
> | :----- | :--- | :----- | ---- |
> | date   | 是   | String | 日期 |

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/news/acg在一起?date=2019-10-20
> 获取成功(200)
``` javascript
{
    "message": "获取acg在一起资讯列表成功",
    "data": [
        {
            "id": 465,
            "title": "《鬼灭之刃》舞台剧定妆照公开！你觉得还原吗？",
            "intro": "根据吾峠呼世晴原作改编的《鬼灭之刃》舞台剧版近日公开了第1弹视觉图以及炭治郎与祢豆子的定妆照，同时公开了演员名单，该舞台剧将于2020年1月18日开演！ 本次的舞台剧中，由小林亮太饰演主人公炭治郎。 …",
            "author": "ACG在一起",
            "cover": "http://tvax1.sinaimg.cn/large/006yt1Omly1g866t1r1r0j312w0q27e5.jpg",
            "refererUrl": "http://acg17.com/52238.html",
            "content": "<p>根据吾峠呼世晴原作改编的《<a href=\"http://acg17.com/tag/%e9%ac%bc%e7%81%ad%e4%b9%8b%e5%88%83/\" title=\"查看所有文章关于 鬼灭之刃\" target=\"_blank\">鬼灭之刃</a>》舞台剧版近日公开了第1弹视觉图以及炭治郎与祢豆子的定妆照，同时公开了演员名单，该舞台剧将于2020年1月18日开演！</p> \n<p>本次的舞台剧中，由小林亮太饰演主人公炭治郎。</p> \n<p><img title=\"《鬼灭之刃》舞台剧定妆照公开！你觉得还原吗？ ACG综合 八卦杂谈 |ACG17\" alt=\"《鬼灭之刃》舞台剧定妆照公开！你觉得还原吗？- ACG17.COM\" src=\"https://tvax1.sinaimg.cn/large/006yt1Omly1g866t1r1r0j312w0q27e5.jpg\" referrerpolicy=\"no-referrer\"></p> \n<p>而饰演祢豆子的是高石明里，看着还不错，个人觉得稍微成熟了点，稍微小一点更萌嘛。</p> \n<p><img title=\"《鬼灭之刃》舞台剧定妆照公开！你觉得还原吗？ ACG综合 八卦杂谈 |ACG17\" alt=\"《鬼灭之刃》舞台剧定妆照公开！你觉得还原吗？- ACG17.COM\" src=\"https://tvax2.sinaimg.cn/large/006yt1Omly1g866str2cfj312w0q2tih.jpg\" referrerpolicy=\"no-referrer\"></p> \n<p>鬼灭之刃在动画化大火后可真是人气超高企划不断，不仅原作卖到脱销，剧场版，第二季、舞台剧也是接踵而来，不过原作漫画最新几集真是跌宕起伏，发盒饭太果断了，不知道后续会怎么样哦。</p> \n<p>其他演员列表如下</p> \n<p>【演员】<br> 灶门炭治郎：小林亮太<br> 灶门祢豆子：高石あかり<br> 我妻善逸：植田圭辅<br> 嘴平伊之助：佐藤祐吾<br> 富冈义勇：本田礼生<br> 鳞泷左近次：高木トモユキ<br> 锖兔：星璃<br> 真菰：其原有沙<br> 白发：柿泽ゆりあ<br> 黑发：久家心<br> 珠世：舞羽美海<br> 愈史郎：佐藤永典<br> 鬼舞辻无惨：佐佐木喜英</p> \n<p><img title=\"《鬼灭之刃》舞台剧定妆照公开！你觉得还原吗？ ACG综合 八卦杂谈 |ACG17\" alt=\"《鬼灭之刃》舞台剧定妆照公开！你觉得还原吗？- ACG17.COM\" src=\"https://tvax3.sinaimg.cn/large/006yt1Omly1g866prca26j30rs13a1bt.jpg\" referrerpolicy=\"no-referrer\"></p> \n<p>&nbsp;</p> \n<p style=\"text-align: center;\"><a href=\"http://acg17.com/52238.html\">ACG17宅一起</a></p> \n<p><img title=\"《鬼灭之刃》舞台剧定妆照公开！你觉得还原吗？ ACG综合 八卦杂谈 |ACG17\" alt=\"《鬼灭之刃》舞台剧定妆照公开！你觉得还原吗？- ACG17.COM\" class=\"aligncenter\" src=\"https://tva2.sinaimg.cn/large/8a1c233bgw1fb6l0gxjk0j20cs077dgj.jpg\" referrerpolicy=\"no-referrer\"></p> \n<div class=\"Copyright\" style=\"border: #f88c00 3px solid; border-radius: 9px; margin-top: 15px; padding-top: 15px; padding-left: 10px; padding-right: 10px;\"> \n <p><i class=\"fa fa-bullhorn\"></i><strong> <a href=\"http://acg17.com/\">ACG17</a> </strong>，欢迎转载，但请保留<strong>链接地址:</strong> <a href=\"http://acg17.com/52238.html\">http://acg17.com/52238.html</a></p> \n <p>内容违规投诉邮箱<img title=\"《鬼灭之刃》舞台剧定妆照公开！你觉得还原吗？ ACG综合 八卦杂谈 |ACG17\" alt=\"《鬼灭之刃》舞台剧定妆照公开！你觉得还原吗？- ACG17.COM\" src=\"https://tva2.sinaimg.cn/large/006yt1Omgy1fm6b4l1rv5j303a00j741.jpg\" referrerpolicy=\"no-referrer\"></p> \n</div>",
            "createDate": "2019-10-20",
            "referer": "ACG在一起"
        },
        {
            "id": 466,
            "title": "《星期一的丰满》偷吃的二人！",
            "intro": "月曜日のたわわ　その２４４ 『犯人はこの中にいる！』 大家喜欢的星期一又来啦！本周登场的是爱酱一家。 时值金秋，气温渐渐下降，爱酱一家纷纷穿上了长袖保暖，这样的日子里，瘫在沙发上看书玩手机可以说是非常 …",
            "author": "ACG在一起",
            "cover": "http://tva3.sinaimg.cn/large/006yt1Omly1g864yh1exjj30jv0rsgti.jpg",
            "refererUrl": "http://acg17.com/52234.html",
            "content": "<p>月曜日のたわわ　その２４４ 『犯人はこの中にいる！』</p> \n<p>大家喜欢的星期一又来啦！本周登场的是爱酱一家。</p> \n<p>时值金秋，气温渐渐下降，爱酱一家纷纷穿上了长袖保暖，这样的日子里，瘫在沙发上看书玩手机可以说是非常舒服的事情了，然而妹妹今天却碰到了让人生气的事情。</p> \n<p>她的零食曲奇饼干不知道被谁偷吃掉了，一个都没剩下。</p> \n<p>“家里的人只有那么几个，所以犯人一定就在爱酱和妈妈之中了”，妹妹一脸怀疑的看着沙发上的两人。</p> \n<p>然而妈妈和爱酱纷纷抬起头，做出一副无辜的样子，那么到底是谁偷吃了饼干呢？</p> \n<p><img title=\"《星期一的丰满》偷吃的二人！ 八卦杂谈 漫画文学 |ACG17\" alt=\"《星期一的丰满》偷吃的二人！- ACG17.COM\" src=\"https://tva3.sinaimg.cn/large/006yt1Omly1g864yh1exjj30jv0rsgti.jpg\" referrerpolicy=\"no-referrer\"></p> \n<p>但是妹妹很快就发现了犯人。</p> \n<p>“真像只有一个！那就是妈妈和爱酱都是偷吃的犯人！”</p> \n<p>虽然你们嘴上擦得很干净，表情也毫无破绽，但是身上却还有饼干的碎屑残留啊，这证据太明显了吧。。。</p> \n<p>&nbsp;</p> \n<p>&nbsp;</p> \n<p>这几天天气冷起来了，听说过几天还会大幅度降温，大家一定要注意保暖，不要感冒了哦。</p> \n<p style=\"text-align: center;\"><a href=\"http://acg17.com/52234.html\">ACG17宅一起</a></p> \n<p><img title=\"《星期一的丰满》偷吃的二人！ 八卦杂谈 漫画文学 |ACG17\" alt=\"《星期一的丰满》偷吃的二人！- ACG17.COM\" class=\"aligncenter\" src=\"https://tva2.sinaimg.cn/large/8a1c233bgw1fb6l0gxjk0j20cs077dgj.jpg\" referrerpolicy=\"no-referrer\"></p> \n<div class=\"Copyright\" style=\"border: #f88c00 3px solid; border-radius: 9px; margin-top: 15px; padding-top: 15px; padding-left: 10px; padding-right: 10px;\"> \n <p><i class=\"fa fa-bullhorn\"></i><strong> <a href=\"http://acg17.com/\">ACG17</a> </strong>，欢迎转载，但请保留<strong>链接地址:</strong> <a href=\"http://acg17.com/52234.html\">http://acg17.com/52234.html</a></p> \n <p>内容违规投诉邮箱<img title=\"《星期一的丰满》偷吃的二人！ 八卦杂谈 漫画文学 |ACG17\" alt=\"《星期一的丰满》偷吃的二人！- ACG17.COM\" src=\"https://tva2.sinaimg.cn/large/006yt1Omgy1fm6b4l1rv5j303a00j741.jpg\" referrerpolicy=\"no-referrer\"></p> \n</div>",
            "createDate": "2019-10-20",
            "referer": "ACG在一起"
        }
    ]
}
```

### 一般业务

#### 1\. 根据画师id查找画师

###### 接口功能
> 根据画师id查找画师


###### URL
> https://api.pixivic.com/artists/{artistId}


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/artists/10
> 获取成功(200)
``` javascript
{
    "message": "获取画师详情成功",
    "data": {
        "id": 10,
        "name": "馬骨",
        "account": "bacotu",
        "avatar": "https://i.pximg.net/user-profile/img/2011/08/16/19/59/25/3500161_6dbc341561aadd4cbf5846ba41311e6e_170.jpg",
        "comment": "馬骨（ばこつ）です。\r\npixiv作った人\r\n\r\nフォロー追加、はずすのもお気軽にどうぞ。\r\n\r\n----\r\n\r\n※何か問題見つけた場合僕にメッセージをするのではなく、\r\n　お問い合わせにお願いします m(_ _)m",
        "gender": "male",
        "birthDay": "",
        "region": "日本 東京都",
        "webPage": "",
        "twitterAccount": "",
        "twitterUrl": "",
        "totalFollowUsers": "214",
        "totalIllustBookmarksPublic": "415"
    }
}
```

#### 2\. 获取画师画作列表

###### 接口功能
> 获取画师画作列表


###### URL
> https://api.pixivic.com/artists/{artistId}/illusts


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> | 参数名   | 必选 | 类型 | 说明 |
> | :------- | :--- | :--- | ---- |
> | page     | 是   | int  |      |
> | pageSize | 是   | int  |      |

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/artists/40773914/illusts
> 获取成功(200)
``` javascript
{
    "message": "获取画师画作列表成功",
    "data": [
        {
            "id": 75957523,
            "artistId": 40773914,
            "title": "犬娘",
            "type": "illust",
            "caption": "だいぶ前に描いたふたなり犬娘",
            "artistPreView": {
                "id": 40773914,
                "name": "湯豆ふ",
                "account": "user_mcyw5435",
                "avatar": "https://i.pximg.net/user-profile/img/2019/05/23/21/37/08/15804454_3937adcb77eb205277b6e5dc26999832_170.png"
            },
            "tags": [
                {
                    "id": null,
                    "name": "R-18",
                    "translatedName": null
                },
                {
                    "id": 41,
                    "name": "オリジナル",
                    "translatedName": "原创"
                },
                {
                    "id": 138270,
                    "name": "ふたなり",
                    "translatedName": "扶他"
                },
                {
                    "id": 175097,
                    "name": "射精",
                    "translatedName": "ejaculation"
                },
                {
                    "id": 1204352,
                    "name": "手コキ",
                    "translatedName": "手交"
                },
                {
                    "id": 3252001,
                    "name": "包茎",
                    "translatedName": "phimosis"
                }
            ],
            "imageUrls": [
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p0_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p0_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p0_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p0.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p1_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p1_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p1_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p1.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p2_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p2_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p2_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p2.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p3_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p3_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p3_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p3.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p4_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p4_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p4_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p4.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p5_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p5_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p5_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p5.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p6_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p6_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p6_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p6.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p7_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p7_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p7_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p7.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p8_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p8_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p8_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p8.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p9_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p9_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p9_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p9.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p10_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p10_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p10_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p10.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p11_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p11_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p11_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p11.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/07/28/21/40/31/75957523_p12_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/07/28/21/40/31/75957523_p12_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/07/28/21/40/31/75957523_p12_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/07/28/21/40/31/75957523_p12.png"
                }
            ],
            "tools": [],
            "createDate": 1564317631000,
            "pageCount": 13,
            "width": 1488,
            "height": 1178,
            "sanityLevel": 6,
            "restrict": 0,
            "totalView": 17543,
            "totalBookmarks": 1859,
            "xrestrict": 1
        },
        {
            "id": 76876198,
            "artistId": 40773914,
            "title": "ふたなり犬娘",
            "type": "illust",
            "caption": "",
            "artistPreView": {
                "id": 40773914,
                "name": "湯豆ふ",
                "account": "user_mcyw5435",
                "avatar": "https://i.pximg.net/user-profile/img/2019/05/23/21/37/08/15804454_3937adcb77eb205277b6e5dc26999832_170.png"
            },
            "tags": [
                {
                    "id": null,
                    "name": "R-18",
                    "translatedName": null
                },
                {
                    "id": 138270,
                    "name": "ふたなり",
                    "translatedName": "扶他"
                },
                {
                    "id": 41,
                    "name": "オリジナル",
                    "translatedName": "原创"
                },
                {
                    "id": 175097,
                    "name": "射精",
                    "translatedName": "ejaculation"
                },
                {
                    "id": 72612,
                    "name": "うちの子",
                    "translatedName": "OC"
                },
                {
                    "id": 37710872,
                    "name": "巨玉",
                    "translatedName": "巨大睾丸"
                },
                {
                    "id": 2550,
                    "name": "犬耳",
                    "translatedName": "dog ears"
                }
            ],
            "imageUrls": [
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/50/25/76876198_p0_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/50/25/76876198_p0_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/50/25/76876198_p0_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/50/25/76876198_p0.png"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/50/25/76876198_p1_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/50/25/76876198_p1_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/50/25/76876198_p1_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/50/25/76876198_p1.png"
                }
            ],
            "tools": [],
            "createDate": 1568973025000,
            "pageCount": 2,
            "width": 1254,
            "height": 1650,
            "sanityLevel": 6,
            "restrict": 0,
            "totalView": 2697,
            "totalBookmarks": 480,
            "xrestrict": 1
        }
    ]
}
```

#### 3\. 获取画作详情

###### 接口功能
> 获取画作详情


###### URL
> https://api.pixivic.com/illusts/{illustId}


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/illusts/76865973
> 获取成功(200)
``` javascript
{
    "message": "获取画作详情成功",
    "data": {
        "id": 76865973,
        "artistId": 34647767,
        "title": "豊乳4989",
        "type": "illust",
        "caption": "9月25日発売の拙著「豊乳4989」の書影です。<br />ご購乳お待ちしております♡",
        "artistPreView": {
            "id": 34647767,
            "name": "琴義弓介",
            "account": "user_tvvj5273",
            "avatar": "https://i.pximg.net/user-profile/img/2018/09/24/23/07/56/14816751_f2f3fced211c39eca09b06e0f1506a0a_170.jpg"
        },
        "tags": [
            {
                "id": 10928,
                "name": "R-18",
                "translatedName": ""
            }
        ],
        "imageUrls": [
            {
                "squareMedium": "https://i.pximg.net/c/540x540_10_webp/img-master/img/2019/09/19/22/53/44/76865973_p0_square1200.jpg",
                "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/19/22/53/44/76865973_p0_master1200.jpg",
                "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/19/22/53/44/76865973_p0_master1200.jpg",
                "original": "https://i.pximg.net/img-original/img/2019/09/19/22/53/44/76865973_p0.jpg"
            }
        ],
        "tools": [],
        "createDate": 1568901224000,
        "pageCount": 1,
        "width": 1224,
        "height": 1738,
        "sanityLevel": 6,
        "restrict": 0,
        "totalView": 4031,
        "totalBookmarks": 621,
        "xrestrict": 1
    }
}
```



### 用户相关

> 一般模块

#### 1\. 获取图片验证码

###### 接口功能
> 获取图片验证码


###### URL
> https://api.pixivic.com/verificationCode


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/verificationCode
> 获取成功(200)
``` javascript
{
    "message": "验证码获取成功",
    "data": {
        "vid": "678a9fb2-4ba2-4035-a9be-1f6156288341",
        "imageBase64": "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"
    }
}
```

#### 2\. 校验邮箱可用性

###### 接口功能
> 校验邮箱可用性（若重复http返回码为409，可用则为404）


###### URL
> httsp://api.pixivic.com/users/emails/{emailAddr}


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：httsp://api.pixivic.com/users/emails/392822872@qq.com
> 获取成功(200)
``` javascript
{
    "message": "邮箱已存在"
}
```

#### 3\. 校验用户名可用性

###### 接口功能
> 校验用户名可用性（若重复http返回码为409，可用则为404）


###### URL
> https://api.pixivic.com/users/usernames/{username}


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/users/usernames/生蚝QAQ
> 获取成功(200)
``` javascript
{
    "message": "用户名不存在"
}
```

#### 4\. 用户注册

###### 接口功能
> 用户注册


###### URL
> https://api.pixivic.com/users


###### 请求方式
> POST

###### 请求头
> 

###### 请求参数
> | 参数名 | 必选 | 类型   | 说明     |
> | :----- | :--- | :----- | -------- |
> | vid    | 是   | String | 验证码id |
> | value  | 是   | String | 验证码值 |

###### 请求体
> ```javascript
> {
> 	"username":"userna1m4541e",
> 	"email":"392822872@qq.com",
> 	"password":"123"
> }
> ```

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/users
> 获取成功(200)
``` javascript
{
    "message": "注册成功",
    "data": "eyJhbGciOiJIUzUxMiJ9.eyJwZXJtaXNzaW9uTGV2ZWwiOjEsInJlZnJlc2hDb3VudCI6MSwiaXNCYW4iOjEsInVzZXJJZCI6MTEsImlhdCI6MTU2ODI5OTA2NiwiZXhwIjoxNTcwMDI3MDY2fQ.uay5Ic_JkPgOrKFjvnTsvtyZmw-bR1o4mL57-4YkajfcOnaLsvQmilLWZhBCVkvlhv-Cx5DV__6FNWLG_PufZg"
}
```

#### 5\. 用户登录

###### 接口功能
> 用户登录（返回头中有携带Authorization，值为token，需要存入cookie）


###### URL
> https://api.pixivic.com/users/token


###### 请求方式
> POST

###### 请求头
> 

###### 请求参数
> | 参数名 | 必选 | 类型   | 说明       |
> | :----- | :--- | :----- | ---------- |
> | vid    | 是   | String | 验证码id   |
> | value  | 是   | String | 验证码的值 |

###### 请求体
> ```javascript
> {
> 	"username":"userna1m4541e",
> 	"password":"123"
> }
> ```

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/spotlights?page=1&pageSize=2 
> 获取成功(200)
``` javascript
{
    "message": "登录成功",
    "data": {
        "userId": 14,
        "username": "oysterqaq1",
        "email": "392822872@qq.com",
        "avatar": "https://img.pixivic.com/defaultAvatar.png",
        "star": 0
    }
}
```

#### 6\. 用户绑定qq

###### 接口功能
> 用户绑定qq


###### URL
> https://api.pixivic.com/{userId}/qqAccessToken


###### 请求方式
> PUT

###### 请求头
> Authorization

###### 请求参数
> | 参数名        | 必选 | 类型   | 说明              |
> | :------------ | :--- | :----- | ----------------- |
> | qqAccessToken | 是   | String | 回调返回的qqtoken |

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/14/qqAccessToken?qqAccessToken=xxxx
> 获取成功(200)
``` javascript
{
    "message": "绑定QQ成功"
}
```

#### 7\. 用户更新头像

###### 接口功能
> 用户更新头像


###### URL
> https://api.pixivic.com/{userId}/avatar


###### 请求方式
> PUT

###### 请求头
> Authorization

###### 请求参数
> | 参数名 | 必选 | 类型   | 说明        |
> | :----- | :--- | :----- | ----------- |
> | avatar | 是   | String | 新的头像url |

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/{14}/avatar
> 获取成功(200)
``` javascript
{
    "message": "修改头像成功",
    "data": "https://111.jpg"
}
```

#### 8\. 发送密码重置邮件

###### 接口功能
> 发送密码重置邮件


###### URL
> https://api.pixivic.com/users/emails/{emailAddr}/resetPasswordEmail


###### 请求方式
> GET

###### 请求头
> 

###### 请求参数
> | 参数名 | 参数位置    | 必选 | 类型   | 说明     |
> | :----- | ----------- | :--- | :----- | -------- |
> | email  | url路径参数 | 是   | String | 用户邮箱 |

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/users/emails/392822872@qq.com/resetPasswordEmail
> 获取成功(200)
``` javascript
{
    "message": "发送密码重置邮件成功"
}
```

#### 9\. 用户重置密码

###### 接口功能
> 用户重置密码


###### URL
> https://api.pixivic.com/users/password


###### 请求方式
> PUT

###### 请求头
> 

###### 请求参数
> | 参数名 | 必选 | 类型   | 说明       |
> | :----- | :--- | :----- | ---------- |
> | vid    | 是   | String | 验证码id   |
> | value  | 是   | String | 验证码的值 |

###### 请求体
> ```javascript
> {
>     "password":"xxx"
> }
> ```

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/users/password 
> 获取成功(200)
``` javascript
{
    "message": "修改密码成功"
}
```

#### 10\. 用户修改密码

###### 接口功能
> 用户修改密码


###### URL
> https://api.pixivic.com/{userId}/password


###### 请求方式
> PUT

###### 请求头
> Authorization

###### 请求参数
> | 参数名   | 必选 | 类型 | 说明 |
> | :------- | :--- | :--- | ---- |
> | page     | 是   | int  |      |
> | pageSize | 是   | int  |      |

###### 请求体
> ```javascript
> {
>     "password":"xxx"
> }
> ```

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/14/password
> 获取成功(200)
``` javascript
{
    "message": "修改密码成功"
}
```

#### 11\. 用户验证新邮箱

###### 接口功能
> 用户验证新邮箱


###### URL
> https://api.pixivic.com/users/emails/{emailAddr}/checkEmail


###### 请求方式
> PUT

###### 请求头
> Authorization

###### 请求参数
> | 参数名 | 必选 | 类型   | 说明   |
> | :----- | :--- | :----- | ------ |
> | email  | 是   | String | 新邮箱 |

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/users/emails/392822872@qq.com/checkEmail
> 获取成功(200)
``` javascript
{
    "message": "发送邮箱验证邮件成功"
}
```



> 业务模块



#### 12\. 用户收藏画作

###### 接口功能
> 用户收藏画作


###### URL
> https://api.pixivic.com/bookmarked


###### 请求方式
> POST

###### 请求头
> Authorization

###### 请求参数
> 

###### 请求体
> ```javascript
> {
> 	"illustId":76876071,
> 	"userId":8
> }
> ```

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/bookmarked
> 成功(200)
``` javascript
{
    "message": "收藏成功"
}
```
> 失败(400)
``` javascript
{
    "message": "用户与画作的收藏关系请求错误"
}
```

#### 13\. 用户获取收藏画作列表

###### 接口功能
> 用户获取收藏画作列表


###### URL
> https://api.pixivic.com/{userId}/bookmarked


###### 请求方式
> GET

###### 请求头
> Authorization

###### 请求参数
> | 参数名   | 必选 | 类型 | 说明     |
> | :------- | :--- | :--- | -------- |
> | page     | 是   | int  | 页数     |
> | pageSize | 否   | int  | 页面大小 |

###### 请求体
> ```javascript
> {
> 	"illustId":76876071,
> 	"userId":8
> }
> ```

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/14/bookmarked
> 成功(200)
``` javascript
{
    "message": "获取收藏画作成功",
    "data": [
        {
            "id": 76876008,
            "artistId": 23928730,
            "title": "もう飼われてました",
            "type": "manga",
            "caption": "ありりんよんこま、3話目です。<br />わんこ大好き凛ちゃんですが、さすがによそさまのわんちゃんには手を出したりしません。ひとのものをとったらどろぼう！<br />最後にはやっぱりありふみの理に導かれてしまいましたが、今後もぜひ絡ませたい二人でした。<br />今度はちゃんとクールな凛ちゃん描きますね…<br /><br />お知らせ：<br />明日からの三連休ですが、ガッツリ予定が入ってしまいましたので投稿はちょっとお休みさせていただきたく思います。<br />台風が予定を吹っ飛ばしたらいつもどおり投稿するかもですけどね。<br />ともあれ、みなさまよき三連休を！🐾",
            "artistPreView": {
                "id": 23928730,
                "name": "みたらしねこ",
                "account": "user_aamr7853",
                "avatar": "https://i.pximg.net/user-profile/img/2018/06/09/08/30/12/14335991_362c8941803505f17455187ad45a7df1_170.png"
            },
            "tags": [
                {
                    "id": 1308,
                    "name": "漫画",
                    "translatedName": "manga"
                },
                {
                    "id": 3634,
                    "name": "4コマ",
                    "translatedName": "4-koma"
                },
                {
                    "id": 2125810,
                    "name": "アイドルマスターシンデレラガールズ",
                    "translatedName": "偶像大师 灰姑娘女孩"
                },
                {
                    "id": 2897759,
                    "name": "橘ありす",
                    "translatedName": "橘爱丽丝"
                },
                {
                    "id": 2176524,
                    "name": "渋谷凛",
                    "translatedName": "涩谷凛"
                },
                {
                    "id": 188,
                    "name": "アイマス100users入り",
                    "translatedName": "偶像大师100收藏"
                },
                {
                    "id": null,
                    "name": "お前は何を言っているんだ",
                    "translatedName": null
                }
            ],
            "imageUrls": [
                {
                    "squareMedium": "https://i.pximg.net/c/540x540_10_webp/img-master/img/2019/09/20/18/33/56/76876008_p0_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/33/56/76876008_p0_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/33/56/76876008_p0_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/33/56/76876008_p0.png"
                }
            ],
            "tools": [],
            "createDate": 1568972036000,
            "pageCount": 1,
            "width": 1200,
            "height": 1890,
            "sanityLevel": 2,
            "restrict": 0,
            "totalView": 3022,
            "totalBookmarks": 235,
            "xrestrict": 0
        },
        {
            "id": 76876028,
            "artistId": 9221468,
            "title": "【月鯉】非番",
            "type": "manga",
            "caption": "ほんの少しだけ本誌ネタっぽいとこもあるのでご注意ください！",
            "artistPreView": {
                "id": 9221468,
                "name": "zygany",
                "account": "zygany",
                "avatar": "https://s.pximg.net/common/images/no_profile.png"
            },
            "tags": [
                {
                    "id": 67547116,
                    "name": "月鯉",
                    "translatedName": "月岛×鲤登"
                },
                {
                    "id": null,
                    "name": "なんてカワイイお話…",
                    "translatedName": null
                },
                {
                    "id": null,
                    "name": "金カム腐1000users入り",
                    "translatedName": null
                }
            ],
            "imageUrls": [
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p0_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p0_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p0_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p0.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p1_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p1_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p1_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p1.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p2_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p2_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p2_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p2.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p3_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p3_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p3_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p3.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p4_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p4_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p4_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p4.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p5_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p5_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p5_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p5.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p6_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p6_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p6_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p6.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p7_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p7_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p7_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p7.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p8_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p8_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p8_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p8.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p9_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p9_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p9_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p9.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p10_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p10_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p10_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p10.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p11_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p11_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p11_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p11.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p12_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p12_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p12_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p12.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p13_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p13_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p13_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p13.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p14_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p14_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p14_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p14.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p15_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p15_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p15_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p15.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p16_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p16_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p16_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p16.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p17_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p17_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p17_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p17.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p18_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p18_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p18_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p18.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p19_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p19_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p19_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p19.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p20_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p20_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p20_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p20.jpg"
                },
                {
                    "squareMedium": "https://i.pximg.net/c/360x360_10_webp/img-master/img/2019/09/20/18/35/56/76876028_p21_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/35/56/76876028_p21_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/35/56/76876028_p21_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/35/56/76876028_p21.jpg"
                }
            ],
            "tools": [],
            "createDate": 1568972156000,
            "pageCount": 22,
            "width": 600,
            "height": 600,
            "sanityLevel": 2,
            "restrict": 0,
            "totalView": 12613,
            "totalBookmarks": 1357,
            "xrestrict": 0
        },
        {
            "id": 76876051,
            "artistId": 501583,
            "title": "恋の呼吸",
            "type": "illust",
            "caption": "",
            "artistPreView": {
                "id": 501583,
                "name": "いくらうに",
                "account": "ikurauni",
                "avatar": "https://i.pximg.net/user-profile/img/2018/08/04/10/58/46/14580782_92063f5ac612ed7349ec8fbfe629e44c_170.png"
            },
            "tags": [
                {
                    "id": 6149871,
                    "name": "鬼滅の刃",
                    "translatedName": "鬼灭之刃"
                },
                {
                    "id": null,
                    "name": "恋柱",
                    "translatedName": null
                },
                {
                    "id": 6793707,
                    "name": "甘露寺蜜璃",
                    "translatedName": "Mitsuri Kanjiro"
                },
                {
                    "id": 6674610,
                    "name": "鬼滅の刃500users入り",
                    "translatedName": "Demon Slayer 500+ bookmarks"
                },
                {
                    "id": 5903,
                    "name": "リブ生地",
                    "translatedName": "粗条纹"
                }
            ],
            "imageUrls": [
                {
                    "squareMedium": "https://i.pximg.net/c/540x540_10_webp/img-master/img/2019/09/20/18/37/55/76876051_p0_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/37/55/76876051_p0_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/37/55/76876051_p0_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/37/55/76876051_p0.png"
                }
            ],
            "tools": [],
            "createDate": 1568972275000,
            "pageCount": 1,
            "width": 1587,
            "height": 2313,
            "sanityLevel": 4,
            "restrict": 0,
            "totalView": 6728,
            "totalBookmarks": 833,
            "xrestrict": 0
        },
        {
            "id": 76876117,
            "artistId": 6395677,
            "title": "Red dress&White dress",
            "type": "illust",
            "caption": "这本来是我给某小说配的插图，不过小说作者没当回事，没看上眼，所以就只能自己发出来给大家当福利了。<br />毕竟人家是大佬，又忙粉丝又多，也不缺我一个233<br /><br />还是感谢大家一直以来的支持，卡蜜酱的粉丝不多，有缘的都是兄弟。等哪天卡蜜酱变得很厉害很厉害了，你们都是我的头号粉丝！<br />卡蜜酱的涩图2群：626956352  （每天照常更新涩图系列）",
            "artistPreView": {
                "id": 6395677,
                "name": "kaga_mi",
                "account": "ln_hyai",
                "avatar": "https://i.pximg.net/user-profile/img/2019/03/17/18/51/50/15533071_f01ec8053c37bf9836fa56b25f89102c_170.jpg"
            },
            "tags": [
                {
                    "id": null,
                    "name": "R-18",
                    "translatedName": null
                },
                {
                    "id": 7649,
                    "name": "拘束",
                    "translatedName": "束缚"
                },
                {
                    "id": 40144,
                    "name": "緊縛",
                    "translatedName": "紧缚"
                },
                {
                    "id": 2984,
                    "name": "タイツ",
                    "translatedName": "裤袜"
                },
                {
                    "id": 3758568,
                    "name": "連縛",
                    "translatedName": "多人捆绑"
                }
            ],
            "imageUrls": [
                {
                    "squareMedium": "https://i.pximg.net/c/540x540_10_webp/img-master/img/2019/09/20/18/43/02/76876117_p0_square1200.jpg",
                    "medium": "https://i.pximg.net/c/540x540_70/img-master/img/2019/09/20/18/43/02/76876117_p0_master1200.jpg",
                    "large": "https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/09/20/18/43/02/76876117_p0_master1200.jpg",
                    "original": "https://i.pximg.net/img-original/img/2019/09/20/18/43/02/76876117_p0.jpg"
                }
            ],
            "tools": [],
            "createDate": 1568972582000,
            "pageCount": 1,
            "width": 2480,
            "height": 3507,
            "sanityLevel": 6,
            "restrict": 0,
            "totalView": 2636,
            "totalBookmarks": 295,
            "xrestrict": 1
        }
    ]
}
```
#### 14\. 用户取消收藏画作

###### 接口功能
> 用户取消收藏画作


###### URL
> https://api.pixivic.com/bookmarked


###### 请求方式
> DELETE

###### 请求头
> Authorization

###### 请求参数
> 

###### 请求体
> ```javascript
> {
> 	"illustId":76876071,
> 	"userId":8
> }
> ```

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/bookmarked
> 成功(200)
``` javascript
{
    "message": "取消收藏成功"
}
```
> 失败(400)
``` javascript
{
    "message": "用户与画作的收藏关系请求错误"
}
```

#### 15\. 查询画作是否被当前用户收藏

###### 接口功能
> 查询画作是否被当前用户收藏


###### URL
> https://api.pixivic.com/{userId}/{illustId}/isBookmarked


###### 请求方式
> GET

###### 请求头
> Authorization

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/8/76876008/isBookmarked
> 成功(200)
``` javascript
{
    "message": "获取是否收藏画作成功",
    "data": true
}
```
> 成功(200)
``` javascript
{
    "message": "获取是否收藏画作成功",
    "data": false
}
```

#### 16\. 用户follow画师

###### 接口功能
> 用户follow画师


###### URL
> https://api.pixivic.com/followed


###### 请求方式
> POST

###### 请求头
> Authorization

###### 请求参数
> 

###### 请求体
> ```javascript
> {
> 	"artistId":10,
> 	"userId":8
> }
> ```

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/bookmarked
> 成功(200)
``` javascript
{
    "message": "follow成功"
}
```

#### 17\. 用户取消follow画师

###### 接口功能
> 用户取消follow画师


###### URL
> https://api.pixivic.com/followed


###### 请求方式
> DELETE

###### 请求头
> Authorization

###### 请求参数
> 

###### 请求体
> ```javascript
> {
> 	"artistId":10,
> 	"userId":8
> }
> ```

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/bookmarked
> 成功(200)
``` javascript
{
    "message": "取消follow成功"
}
```

#### 18\. 获取用户收藏画师列表

###### 接口功能
> 获取用户收藏画师列表


###### URL
> https://api.pixivic.com/{userId}/followed


###### 请求方式
> GET

###### 请求头
> Authorization

###### 请求参数
> | 参数名   | 必选 | 类型 | 说明     |
> | :------- | :--- | :--- | -------- |
> | page     | 是   | int  | 页数     |
> | pageSize | 否   | int  | 页面大小 |

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/14/followed
> 成功(200)
``` javascript
{
    "message": "获取follow画师列表成功",
    "data": [
        {
            "id": 10,
            "name": "馬骨",
            "account": "bacotu",
            "avatar": "https://i.pximg.net/user-profile/img/2011/08/16/19/59/25/3500161_6dbc341561aadd4cbf5846ba41311e6e_170.jpg",
            "comment": "馬骨（ばこつ）です。\r\npixiv作った人\r\n\r\nフォロー追加、はずすのもお気軽にどうぞ。\r\n\r\n----\r\n\r\n※何か問題見つけた場合僕にメッセージをするのではなく、\r\n　お問い合わせにお願いします m(_ _)m",
            "gender": "male",
            "birthDay": "",
            "region": "日本 東京都",
            "webPage": "",
            "twitterAccount": "",
            "twitterUrl": "",
            "totalFollowUsers": "214",
            "totalIllustBookmarksPublic": "415"
        }
    ]
}
```

#### 19\. 获取用户是否follow画师

###### 接口功能
> 获取用户是否follow画师


###### URL
> https://api.pixivic.com/{userId}/{artistId}/isFollowed


###### 请求方式
> GET

###### 请求头
> Authorization

###### 请求参数
> 

###### 请求体
> 

###### 返回字段
> 参照返回示例

###### 接口示例
> 地址：https://api.pixivic.com/8/10/isFollowed
> 成功(200)
``` javascript
{
    "message": "获取是否follow画师成功",
    "data": true
}
```
