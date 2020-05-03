package dev.cheerfun.pixivic.biz.web.collection.mapper;

import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import dev.cheerfun.pixivic.biz.web.collection.po.CollectionTag;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CollectionMapper {

    void updateIllustrationOrder(Integer collectionId, Integer reOrderIllustrationId, Integer resultIndex);

    Integer incrIllustrationInsertFactor(Integer collectionId, Integer upIllustrationId);

    void reOrderIllustration(Integer collectionId);

    @Insert("insert into collections (user_id, username,title,cover,caption,tag_list,is_public,forbid_comment,porn_warning) " +
            "values (#{userId}, #{collection.username}, #{collection.title}, #{collection.cover,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}, #{collection.caption},#{collection.tagList,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler},#{collection.isPublic}, #{collection.forbidComment}, #{collection.pornWarning})")
    Integer createCollection(@Param("userId") Integer userId, @Param("collection") Collection collection);

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
            "update collections",
            "<if test=\"collection.cover!=null\">\n",
            "AND cover=#{collection.cover,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}\n",
            "</if>",
            "<if test=\"collection.title!=null\">\n",
            "AND title=#{collection.title}\n",
            "</if>",
            "<if test=\"collection.caption!=null\">\n",
            "AND caption=#{collection.caption}\n",
            "</if>",
            "<if test=\"collection.tagList!=null\">\n",
            "AND tag_list=#{collection.tagList,typeHandler=dev.cheerfun.pixivic.common.util.json.JsonTypeHandler}\n",
            "</if>",
            "<if test=\"collection.isPublic!=null\">\n",
            "AND is_public=#{collection.isPublic}\n",
            "</if>",
            "<if test=\"collection.forbidComment!=null\">\n",
            "AND forbid_comment=#{collection.forbidComment}\n",
            "</if>",
            "<if test=\"collection.pornWarning!=null\">\n",
            "AND porn_warning=#{collection.pornWarning}\n",
            "</if>",
            "where user_id=#{userId} and collection_id=#{collection.id}",
            "</script>"
    })
    void updateCollection(@Param("userId") Integer userId, @Param("collection") Collection collection);

    @Update("update collections set use_flag=0 where user_id=#{userId} and collection_id=#{collectionId}")
    Integer deleteCollection(Integer userId, Integer collectionId);

    @Insert("update collections set illust_count=illust_count+1 where collection_id=#{collectionId};" +
            "insert into collection_illust_relation  (collection_id, illust_id,order_num) values (#{collectionId}, #{illustrationId},(select illust_count from collections where collection_id=#{collectionId}))")
    void addIllustrationToCollection(Integer collectionId, Integer illustrationId);

    Integer checkCollectionAuth(Integer collectionId, Integer userId);
}
