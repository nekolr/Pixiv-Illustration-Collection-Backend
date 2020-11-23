package dev.cheerfun.pixivic.biz.web.collection.mapper;

import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import dev.cheerfun.pixivic.biz.web.collection.po.CollectionTag;
import dev.cheerfun.pixivic.common.po.illust.ImageUrl;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CollectionMapper {
    @Update("update collection_illust_relation set order_num=#{resultIndex} where collection_id=#{collectionId} and illust_id=#{reOrderIllustrationId}")
    Integer updateIllustrationOrder(Integer collectionId, Integer reOrderIllustrationId, Integer resultIndex);

    @Update("update collection_illust_relation set insert_factor=insert_factor+1 where collection_id=#{collectionId} and illust_id=#{upIllustrationId}")
    Integer incrIllustrationInsertFactor(Integer collectionId, Integer upIllustrationId);

    @Update("update collection_illust_relation c ,\n" +
            "    (select illust_id, (@rowNum := @rowNum + 1) order_num\n" +
            "     from collection_illust_relation,\n" +
            "          (Select (@rowNum := 0)) b\n" +
            "     where collection_id = #{collectionId}\n" +
            "     order by order_num) t\n" +
            "set c.order_num=t.order_num * 10000,\n" +
            "    c.insert_factor=0\n" +
            "where c.collection_id = #{collectionId}\n" +
            "  and c.illust_id = t.illust_id")
    Integer reOrderIllustration(Integer collectionId);

    @Insert("insert into collections (user_id, username,title,cover,caption,tag_list,is_public,forbid_comment,porn_warning,create_time) " +
            "values (#{userId}, #{username}, #{title}, #{cover,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}, #{caption},#{tagList,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler},#{isPublic}, #{forbidComment}, #{pornWarning}, #{createTime,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "collection_id")
    Integer createCollection(Collection collection);

    @Insert({
            "<script>",
            "replace into collection_tag (`tag_id`, `tag_name`) values ",
            "<foreach collection='tagList' item='tag' index='index' separator=','>",
            "(#{tag.id}, #{tag.tagName})",
            "</foreach>",
            "</script>"
    })
    void insertCollectionTag(@Param("tagList") List<CollectionTag> tagList);

    @Update({
            "<script>",
            "update collections ",
            "<set>",
            "<if test=\"collection.cover!=null\">\n",
            "cover=#{collection.cover,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler},",
            "</if>",
            "<if test=\"collection.title!=null\">\n",
            "title=#{collection.title},",
            "</if>",
            "<if test=\"collection.caption!=null\">\n",
            "caption=#{collection.caption},",
            "</if>",
            "<if test=\"collection.tagList!=null\">\n",
            "tag_list=#{collection.tagList,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler},",
            "</if>",
            "<if test=\"collection.isPublic!=null\">\n",
            "is_public=#{collection.isPublic},",
            "</if>",
            "<if test=\"collection.forbidComment!=null\">\n",
            "forbid_comment=#{collection.forbidComment},",
            "</if>",
            "<if test=\"collection.pornWarning!=null\">\n",
            "porn_warning=#{collection.pornWarning},",
            "</if>",
            "</set>",
            "where user_id=#{userId} and collection_id=#{collection.id}",
            "</script>"
    })
    void updateCollection(@Param("userId") Integer userId, @Param("collection") Collection collection);

    @Update("update collections set use_flag=0 where user_id=#{userId} and collection_id=#{collectionId}")
    Integer deleteCollection(Integer userId, Integer collectionId);

    @Update("update collections set illust_count=illust_count-1 where collection_id=#{collectionId}")
    void decrCollectionIllustCount(Integer collectionId);

    @Update("update collections set illust_count=illust_count+#{sum} where collection_id=#{collectionId}")
    void incrCollectionIllustCount(Integer collectionId, int sum);

    @Insert("insert into collection_illust_relation  (collection_id, illust_id,order_num) values (#{collectionId}, #{illustrationId},(select illust_count from collections where collection_id=#{collectionId}))")
    Integer addIllustrationToCollection(Integer collectionId, Integer illustrationId);

    @Select("   SELECT IFNULL(( " +
            "                    SELECT 1 " +
            "                    FROM collections " +
            "                    WHERE user_id=#{userId} and collection_id=#{collectionId}" +
            "                    LIMIT 1),0)")
    Integer checkCollectionAuth(Integer collectionId, Integer userId);

    @Delete("delete from collection_illust_relation where  collection_id= #{collectionId} and illust_id=#{illustrationId} ")
    Integer deleteIllustrationFromCollection(Integer collectionId, Integer illustrationId);

    @Select("select order_num from collection_illust_relation where collection_id= #{collectionId} and illust_id=#{illustrationId}")
    Integer queryIllustrationOrder(Integer collectionId, Integer illustrationId);

    @Select("select insert_factor from collection_illust_relation where collection_id= #{collectionId} and illust_id=#{upIllustrationId}")
    Integer queryIllustrationInsertFactor(Integer collectionId, Integer upIllustrationId);

    @Select({
            "<script>",
            "select collection_id from collections where use_flag=1 and  is_public ",
            "<if test=\"isSelf==0\">\n",
            "= 1",
            "</if>",
            "<if test=\"isSelf==1\">\n",
            "<if test=\"isPublic==null\">\n",
            "in (0,1)",
            "</if>",
            "<if test=\"isPublic!=null\">\n",
            "= #{isPublic}",
            "</if>",
            "</if>",
            "and user_id=#{userId} order by #{orderBy} #{orderByMode} limit #{currIndex},#{pageSize}",
            "</script>"
    })
    List<Integer> queryUserCollection(Integer userId, Integer currIndex, Integer pageSize, Integer isSelf, Integer isPublic, String orderBy, String orderByMode);

    @Select("select * from collections where collection_id=#{collectionId} and use_flag=1")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "id", column = "collection_id"),
            @Result(property = "tagList", column = "tag_list", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "cover", column = "cover", javaType = List.class, typeHandler = JsonTypeHandler.class),
            @Result(property = "createTime", column = "create_time", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class),
            @Result(property = "isPublic", column = "is_public"),
            @Result(property = "useFlag", column = "use_flag"),
            @Result(property = "forbidComment", column = "forbid_comment"),
            @Result(property = "pornWarning", column = "porn_warning"),
            @Result(property = "totalBookmarked", column = "total_bookmarked"),
            @Result(property = "totalView", column = "total_view"),
            @Result(property = "totalPeopleSeen", column = "total_people_seen"),
            @Result(property = "totalLiked", column = "total_liked"),
            @Result(property = "totalReward", column = "total_reward"),
            @Result(property = "illustCount", column = "illust_count"),
    })
    Collection queryCollectionById(Integer collectionId);

    @Select("select illust_id from collection_illust_relation where collection_id =#{collectionId} limit #{currIndex},#{pageSize}")
    List<Integer> queryCollectionIllustIdList(Integer collectionId, Integer currIndex, Integer pageSize);

    @Select("select collection_id from collections where use_flag=1 and  is_public=1 order by create_time limit #{currIndex},#{pageSize} ")
    List<Integer> queryLatestPublicCollection(Integer currIndex, Integer pageSize);

    @Select("select collection_id from collections where use_flag=1 and  is_public=1 order by total_bookmark limit #{currIndex},#{pageSize} ")
    List<Integer> queryPopPublicCollection(int currIndex, Integer pageSize);

    @Update("update collections set cover=#{imageUrlList,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler} where collection_id=#{collectionId}")
    Integer updateCollectionCover(Integer collectionId, List<ImageUrl> imageUrlList);

    @Delete("delete from user_collection_bookmarked where collection_id = #{collectionId}")
    Integer deleteCollectionBookmark(Integer collectionId);

    @Update("update collections set total_bookmark=total_bookmark-1 where collection_id =  #{collectionId} ")
    Integer decrCollectionTotalBookmark(Integer collectionId);

    @Update("update collections set total_bookmark=total_bookmark+1 where collection_id =  #{collectionId} ")
    Integer incrCollectionTotalBookmark(Integer collectionId);

    @Update("update collections set total_liked=total_liked+1 where collection_id =  #{collectionId} ")
    Integer incrCollectionTotalLike(Integer collectionId);

    @Update("update collections set total_liked=total_liked-1 where collection_id =  #{collectionId} ")
    Integer decrCollectionTotalLike(Integer collectionId);

    @Update("update user_collection_summary\n" +
            "set private_collection_sum=(select count(*)\n" +
            "                            from collections\n" +
            "                            where use_flag = 1 and is_public = 1 and collections.user_id = #{userId})\n" +
            "where user_id = #{userId}")
    Integer dealUserPublicCollectionSummary(Integer userId);

    @Update("update user_collection_summary\n" +
            "set private_collection_sum=(select count(*)\n" +
            "                            from collections\n" +
            "                            where use_flag = 1 and is_public = 0 and collections.user_id = #{userId})\n" +
            "where user_id = #{userId}")
    Integer dealUserPrivateCollectionSummary(Integer userId);

    @Select({
            "<script>",
            "select ",
            "<if test=\"isPublic==0\">\n",
            "private_collection_sum",
            "</if>",
            "<if test=\"isPublic==1\">\n",
            "public_collection_sum",
            "</if>",
            "<if test=\"isPublic==null\">\n",
            "public_collection_sum+private_collection_sum",
            "</if>",
            "from user_collection_summary where user_id=#{userId}",
            "</script>"
    })
    Integer queryCollectionSummary(Integer userId, Integer isPublic);

    @Update("update collections\n" +
            "set total_bookmarked=#{totalBookmarked},\n" +
            "    total_liked=#{totalLiked},\n" +
            "    total_people_seen=#{totalPeopleSeen}\n" +
            "where collection_id = #{collectionId}")
    Integer dealStaticInfo(Integer collectionId, Integer totalBookmarked, Integer totalLiked, Integer totalPeopleSeen);

    @Update("update user_collection_summary\n" +
            "set bookmark_collection_sum = bookmark_collection_sum + #{modify}\n" +
            "where user_id = #{userId}")
    Integer modifyUserTotalBookmarkCollection(Integer userId, int modify);

    @Select("select bookmark_collection_sum\n" +
            "from user_collection_summary\n" +
            "where user_id = #{userId}")
    Integer queryUserTotalBookmarkCollection(Integer userId);

    @Delete("delete\n" +
            "from collection_illust_relation\n" +
            "where collection_id = #{collectionId}\n" +
            "  and order_num between 0 and #{size}")
    void deleteIllustrationByIndexFromCollection(Integer collectionId, Integer size);

    @Insert({
            "<script>",
            "insert IGNORE into collection_illust_relation(collection_id, illust_id,order_num) values ",
            "<foreach collection='illustIdList' item='illustId' index='index' separator=','>",
            "(#{collectionId},#{illustId}, #{index})",
            "</foreach>",
            "</script>"
    })
    void insertIllustrationByIndexToCollection(Integer collectionId, List<Integer> illustIdList);

    @Select("select count(*) from collections where use_flag = 1 and is_public = 0 ")
    Integer queryPublicCollectionCount();

    @Select("select collection_id from collections where illust_count>0 and cover='[]'")
    List<Integer> queryAllCollectionWithoutCover();

}
