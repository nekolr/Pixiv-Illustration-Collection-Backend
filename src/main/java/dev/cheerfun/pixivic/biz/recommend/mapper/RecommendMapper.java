package dev.cheerfun.pixivic.biz.recommend.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecommendMapper {
/*    select a.user_id, a.illust_id, b.total_bookmarks
    from user_illust_bookmarked a,
    illusts b
    where a.illust_id = b.illust_id
    ;
    select a.user_id, a.illust_id, b.total_bookmarks
    from illust_history a,
    illusts b
    where a.illust_id = b.illust_id
    ;
    select user_id, artist_id
    from user_artist_followed;*/

  /*  queryUserIllustLikeData();
    queryUserIllustViewData();
    queryUserArtistFollowData();*/

    //根据活跃度查询用户
}
