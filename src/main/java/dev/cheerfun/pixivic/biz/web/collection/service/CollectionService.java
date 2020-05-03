package dev.cheerfun.pixivic.biz.web.collection.service;

import dev.cheerfun.pixivic.basic.sensitive.util.SensitiveFilter;
import dev.cheerfun.pixivic.biz.web.collection.dto.UpdateIllustrationOrderDTO;
import dev.cheerfun.pixivic.biz.web.collection.mapper.CollectionMapper;
import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import dev.cheerfun.pixivic.biz.web.collection.po.CollectionTag;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static dev.cheerfun.pixivic.common.constant.RedisKeyConstant.COLLECTION_REORDER_LOCK;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/5/2 7:55 下午
 * @description CollectionService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CollectionService {
    private final CollectionMapper collectionMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final SensitiveFilter sensitiveFilter;

    public Boolean createCollection(Integer userId, Collection collection) {
        //去除敏感词
        collection.getTagList().forEach(e -> {
            e.setTagName(sensitiveFilter.filter(e.getTagName()));
        });
        collection.setCreateTime(LocalDateTime.now());
        //插入画集
        collectionMapper.createCollection(userId, collection);
        //异步将tag入库
        insertCollectionTag(collection);
        return true;
    }

    @Async
    public void insertCollectionTag(Collection collection) {
        List<CollectionTag> tagList = collection.getTagList();
        if (tagList != null && tagList.size() > 0) {
            collectionMapper.insertCollectionTag(tagList);
        }
    }

    @Transactional
    public Boolean updateCollection(Integer userId, Collection collection) {
        if (collection.getId() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "更新画集出错");
        }
        //校验collectionId是否属于用户
        checkCollectionAuth(collection.getId(), userId);
        collectionMapper.updateCollection(userId, collection);
        //异步将tag入库
        insertCollectionTag(collection);
        return true;
    }

    public Boolean deleteCollection(Integer userId, Integer collectionId) {
        return collectionMapper.deleteCollection(userId, collectionId) == 1;
    }

    @Transactional
    public Boolean addIllustrationToCollection(Integer userId, Integer collectionId, Illustration illustration) {
        //校验collectionId是否属于用户
        checkCollectionAuth(collectionId, userId);
        //插入
        collectionMapper.incrCollectionIllustCount(collectionId);
        collectionMapper.addIllustrationToCollection(collectionId, illustration.getId());
        return true;
    }

    @Transactional
    public Boolean deleteIllustrationFromCollection(Integer userId, Integer collectionId, Integer illustrationId) {
        //校验collectionId是否属于用户
        checkCollectionAuth(collectionId, userId);
        //删除
        collectionMapper.decrCollectionIllustCount(collectionId);
        collectionMapper.deleteIllustrationFromCollection(collectionId, illustrationId);
        return true;
    }

    public boolean checkCollectionAuth(Integer collectionId, Integer userId) {
        if (collectionMapper.checkCollectionAuth(collectionId, userId) == 1) {
            return true;
        }
        throw new BusinessException(HttpStatus.FORBIDDEN, "没有修改画集的权限");

    }

    public boolean checkCollectionUpdateStatus(Integer collectionId) {
        return stringRedisTemplate.opsForValue().setIfAbsent(COLLECTION_REORDER_LOCK + collectionId, "Y", 10, TimeUnit.SECONDS);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateIllustrationOrder(Integer collectionId, UpdateIllustrationOrderDTO updateIllustrationOrderDTO, Integer userId) {
        long start = System.currentTimeMillis();
        System.out.println(start);
        //校验collectionId是否属于用户
        checkCollectionAuth(collectionId, userId);
        //输入三个illust对象，分别是要插入位置的上下两个 以及 插入对象
        //查看要插入的画作是否在画集中
        Integer illustrationOrder = queryIllustrationOrder(collectionId, updateIllustrationOrderDTO.getReOrderIllustrationId());
        if (illustrationOrder == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "画作不在画集中");
        }
        //带有超时的cas
        long now = System.currentTimeMillis();
        while (System.currentTimeMillis() - now < 10 * 1000) {
            //尝试加锁
            if (checkCollectionUpdateStatus(collectionId)) {
                continue;
            }
            Integer upperIndex;
            Integer lowerIndex;
            Integer originalIndex;
            Integer resultIndex;
            //查出上界
            try {
                originalIndex = queryIllustrationOrder(collectionId, updateIllustrationOrderDTO.getReOrderIllustrationId());
                upperIndex = queryIllustrationOrder(collectionId, updateIllustrationOrderDTO.getUpIllustrationId());
                if (upperIndex == null) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "画作不在画集中");
                }
                //取中值
                if (updateIllustrationOrderDTO.getLowIllustrationId() == null) {
                    //无下界，直接上界+1w
                    resultIndex = upperIndex + 10000;
                } else {
                    //查出下界index
                    lowerIndex = queryIllustrationOrder(collectionId, updateIllustrationOrderDTO.getLowIllustrationId());
                    if (lowerIndex == null) {
                        throw new BusinessException(HttpStatus.BAD_REQUEST, "画作不在画集中");
                    }
                    resultIndex = (upperIndex + lowerIndex) / 2;
                }
                if (resultIndex.compareTo(originalIndex) != 0) {
                    //更新
                    collectionMapper.updateIllustrationOrder(collectionId, updateIllustrationOrderDTO.getReOrderIllustrationId(), resultIndex);
                    //并更改上界插入因子
                    collectionMapper.incrIllustrationInsertFactor(collectionId, updateIllustrationOrderDTO.getUpIllustrationId());
                    Integer insertFactor = collectionMapper.queryIllustrationInsertFactor(collectionId, updateIllustrationOrderDTO.getUpIllustrationId());
                    //判断上界是否达到阈值
                    if (insertFactor >= 10) {
                        //达到则进行全量更新，并把插入因子都置为0
                        collectionMapper.reOrderIllustration(collectionId);
                    }
                }
                System.out.println("耗时：" + (System.currentTimeMillis() - start));
                return true;
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new BusinessException(HttpStatus.EXPECTATION_FAILED, "未知异常");
            } finally {
                stringRedisTemplate.delete(COLLECTION_REORDER_LOCK + collectionId);
            }

        }
        return false;
    }

    private Integer queryIllustrationOrder(Integer collectionId, Integer illustrationId) {

        return collectionMapper.queryIllustrationOrder(collectionId, illustrationId);
    }

    public List<Collection> queryUserCollection(Integer userId) {
        return null;
    }
}
