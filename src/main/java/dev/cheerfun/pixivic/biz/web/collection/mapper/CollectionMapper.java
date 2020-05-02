package dev.cheerfun.pixivic.biz.web.collection.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CollectionMapper {

    void updateIllustrationOrder(Integer collectionId, Integer reOrderIllustrationId, Integer resultIndex);

    Integer incrIllustrationInsertFactor(Integer collectionId, Integer upIllustrationId);

    void reOrderIllustration(Integer collectionId);
}
