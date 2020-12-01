package dev.cheerfun.pixivic.biz.web.collection.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.basic.sensitive.util.SensitiveFilter;
import dev.cheerfun.pixivic.biz.web.collection.dto.CollectionDigest;
import dev.cheerfun.pixivic.biz.web.collection.dto.UpdateIllustrationOrderDTO;
import dev.cheerfun.pixivic.biz.web.collection.mapper.CollectionMapper;
import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import dev.cheerfun.pixivic.biz.web.collection.po.CollectionTag;
import dev.cheerfun.pixivic.biz.web.collection.util.CollectionSearchUtil;
import dev.cheerfun.pixivic.biz.web.collection.util.CollectionTagSearchUtil;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ImageUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static dev.cheerfun.pixivic.common.constant.RedisKeyConstant.COLLECTION_REORDER_LOCK;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/5/2 7:55 下午
 * @description CollectionService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CollectionService {
    private final CollectionMapper collectionMapper;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final SensitiveFilter sensitiveFilter;
    private final IllustrationBizService illustrationBizService;
    private final CollectionTagSearchUtil collectionTagSearchUtil;
    private final CollectionSearchUtil collectionSearchUtil;

    @Caching(evict = {
            @CacheEvict(value = "user_collection_digest_list", key = "#userId+'-0'"),
            @CacheEvict(value = "user_collection_digest_list", key = "#userId+'-null'"),
            @CacheEvict(value = "user_collection_digest_list", key = "#userId+'-1'"),
            @CacheEvict("publicCollectionCount")
    })
    public Integer createCollection(Integer userId, Collection collection) {
        //去除敏感词
        List<CollectionTag> tagList = collection.getTagList();
        if (tagList != null && tagList.size() > 0) {
            collection.setTagList(tagList.stream().filter(e -> {
                if (e.getTagName() != null && e.getTagName().length() > 0) {
                    e.setTagName(sensitiveFilter.filter(e.getTagName()));
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList()));
        }
        collection.setCreateTime(LocalDateTime.now());
        //插入画集
        collection.setUserId(userId);
        collectionMapper.createCollection(collection);
        //更新汇总
        dealUserCollectionSummary(userId);
        //异步将tag入库
        //insertCollectionTag(collection);
        return collection.getId();
    }

    @Async("saveToDBExecutorService")
    public void insertCollectionTag(Collection collection) {
        List<CollectionTag> tagList = collection.getTagList();
        if (tagList != null && tagList.size() > 0) {
            collectionMapper.insertCollectionTag(tagList);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "collections", key = "#collection.id"),
            @CacheEvict("publicCollectionCount")
    })
    public Boolean updateCollection(Integer userId, Collection collection) {
        if (collection.getId() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "更新画集出错");
        }
        //校验collectionId是否属于用户
        checkCollectionAuth(collection.getId(), userId);
        List<CollectionTag> tagList = collection.getTagList();
        if (tagList != null && tagList.size() > 0) {
            collection.setTagList(tagList.stream().filter(e -> {
                if (e.getTagName() != null && e.getTagName().length() > 0) {
                    e.setTagName(sensitiveFilter.filter(e.getTagName()));
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList()));
        }
        collectionMapper.updateCollection(userId, collection);
        //是否修改了可见性
        //修改则清空收藏数以及收藏数据
        //更新汇总
        dealUserCollectionSummary(userId);
        return true;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "collections", key = "#collectionId"),
            @CacheEvict("publicCollectionCount"),
            @CacheEvict(value = "user_collection_digest_list", key = "#userId+'-0'"),
            @CacheEvict(value = "user_collection_digest_list", key = "#userId+'-null'"),
            @CacheEvict(value = "user_collection_digest_list", key = "#userId+'-1'"),
    })
    public Boolean deleteCollection(Integer userId, Integer collectionId) {
        //删除收藏、点赞、浏览量数据
        if (collectionMapper.deleteCollection(userId, collectionId) == 1) {
            //mysql清除
            collectionMapper.deleteCollectionBookmark(collectionId);
            //更新汇总
            dealUserCollectionSummary(userId);
            //redis清除
            stringRedisTemplate.delete(RedisKeyConstant.COLLECTION_BOOKMARK_REDIS_PRE + collectionId);
            stringRedisTemplate.delete(RedisKeyConstant.COLLECTION_LIKE_REDIS_PRE + collectionId);
            stringRedisTemplate.delete(RedisKeyConstant.COLLECTION_TOTAL_PEOPLE_SEEN_REDIS_PRE + collectionId);
        }
        return true;
    }

    @Transactional
    @CacheEvict(value = "collections", key = "#collectionId")
    public List<Integer> addIllustrationToCollection(Integer userId, Integer collectionId, List<Integer> illustrationIds) {
        //List<Integer> success=new ArrayList<>();
        List<Integer> failed = new ArrayList<>();
        //校验collectionId是否属于用户
        checkCollectionAuth(collectionId, userId);
        Collection collection = queryCollectionByIdFromDb(collectionId);
        //插入
        int sum = 0;
        for (Integer illustrationId : illustrationIds) {
            try {
                collectionMapper.addIllustrationToCollection(collectionId, illustrationId);
                sum++;
            } catch (DuplicateKeyException e) {
                log.info("画作：" + illustrationId + "在画集：" + collectionId + "中已经存在");
                failed.add(illustrationId);
            }
        }
        collectionMapper.incrCollectionIllustCount(collectionId, sum);
        if (collection.getIllustCount() == 0 || collection.getCover() == null || collection.getCover().size() == 0) {
            List<ImageUrl> imageUrlList = illustrationBizService.queryIllustrationByIdList(illustrationIds).stream().limit(5).map(i -> i.getImageUrls().get(0)).collect(Collectors.toList());
            collectionMapper.updateCollectionCover(collectionId, imageUrlList);
        }
        return failed;
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

    @Transactional
    public Boolean deleteIllustrationFromCollection(Integer userId, Integer collectionId, List<Integer> illustrationIdList) {
        illustrationIdList.forEach(e -> deleteIllustrationFromCollection(userId, collectionId, e));
        return true;
    }

    @Cacheable("collectionAuth")
    public boolean checkCollectionAuth(Integer collectionId, Integer userId) {
        Collection collection = queryCollectionByIdFromDb(collectionId);
        if (collection.getUserId().compareTo(userId) == 0) {
            return true;
        }
        throw new BusinessException(HttpStatus.FORBIDDEN, "没有修改画集的权限");

    }

    public boolean checkCollectionUpdateStatus(Integer collectionId) {
        return stringRedisTemplate.opsForValue().setIfAbsent(COLLECTION_REORDER_LOCK + collectionId, "Y", 10, TimeUnit.SECONDS);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateIllustrationOrder(Integer collectionId, List<Integer> illustIdList, Integer userId) {
        //校验collectionId是否属于用户
        checkCollectionAuth(collectionId, userId);
        //删除0到size的联系数据
        collectionMapper.deleteIllustrationByIndexFromCollection(collectionId, illustIdList.size());
        //插入数据
        collectionMapper.insertIllustrationByIndexToCollection(collectionId, illustIdList);
        return true;
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
                log.info("耗时：" + (System.currentTimeMillis() - start));
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

    public List<Collection> queryUserCollection(Integer userId, Integer isSelf, Integer isPublic, Integer page, Integer pageSize, String orderBy, String orderByMode) {
        List<Integer> collectionIdList = collectionMapper.queryUserCollection(userId, (page - 1) * pageSize, pageSize, isSelf, isPublic, orderBy, orderByMode);
        return queryCollectionById(collectionIdList);
    }

    public List<Illustration> queryCollectionIllust(Integer collectionId, Integer page, Integer pageSize) {
        //校验画集是否公开 画集isPublic 或者user是本人
        Collection collection = queryCollectionById(collectionId);
        if (collection == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "未找到该画集");
        }
        if (collection.getIsPublic() == 0) {
            Map<String, Object> context = AppContext.get();
            if (context != null && context.get(AuthConstant.USER_ID) != null) {
                if ((int) context.get(AuthConstant.USER_ID) != collection.getUserId()) {
                    throw new BusinessException(HttpStatus.FORBIDDEN, "无权限访问");
                }
            }
        }
        List<Integer> illustIdList = collectionMapper.queryCollectionIllustIdList(collectionId, (page - 1) * pageSize, pageSize);
        return illustrationBizService.queryIllustrationByIllustIdList(illustIdList);
    }

    public Collection queryCollectionById(Integer collectionId) {
        Collection collection = queryCollectionByIdFromDb(collectionId);
        if (collection != null) {
            pullStaticInfo(collection);
        }
        return collection;
    }

    @Cacheable(value = "collections", key = "#collectionId")
    public Collection queryCollectionByIdFromDb(Integer collectionId) {
        Collection collection = collectionMapper.queryCollectionById(collectionId);
        collection = objectMapper.convertValue(collection, new TypeReference<Collection>() {
        });
        return collection;
    }

    public List<Collection> queryCollectionById(List<Integer> collectionId) {
        return collectionId.stream().parallel().filter(Objects::nonNull).map(this::queryCollectionById).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Collection> queryLatestPublicCollection(Integer page, Integer pageSize) {
        List<Integer> collectionIdList = collectionMapper.queryLatestPublicCollection((page - 1) * pageSize, pageSize);
        return queryCollectionById(collectionIdList);
    }

    public List<Collection> queryPopPublicCollection(Integer page, Integer pageSize) {
        List<Integer> collectionIdList = collectionMapper.queryPopPublicCollection((page - 1) * pageSize, pageSize);
        return queryCollectionById(collectionIdList);
    }

    @Cacheable("publicCollectionCount")
    public Integer queryPublicCollectionCount() {
        return collectionMapper.queryPublicCollectionCount();
    }

    @Cacheable("collection_tag_search")
    public CompletableFuture<List<CollectionTag>> autocompleteCollectionTag(String keyword) {
        return collectionTagSearchUtil.search(keyword);
    }

    @Async("saveToDBExecutorService")
    public void modifyTotalBookmark(Integer collectionId, Integer modify) {
        if (modify > 0) {
            collectionMapper.incrCollectionTotalBookmark(collectionId);
        } else {
            collectionMapper.decrCollectionTotalBookmark(collectionId);
        }
    }

    @Async("saveToDBExecutorService")
    public void modifyLikeCount(Integer collectionId, Integer modify) {
        if (modify > 0) {
            collectionMapper.incrCollectionTotalLike(collectionId);
        } else {
            collectionMapper.decrCollectionTotalLike(collectionId);
        }
    }

    @Async("saveToDBExecutorService")
    public void dealStaticInfo(Integer collectionId, Integer totalBookmarked, Integer totalLiked, Integer totalPeopleSeen) {
        collectionMapper.dealStaticInfo(collectionId, totalBookmarked, totalLiked, totalPeopleSeen);
    }

    public void modifyCollectionTotalPeopleSeen(Integer collectionId, String userFinger) {
        stringRedisTemplate.opsForHyperLogLog().add(RedisKeyConstant.COLLECTION_TOTAL_PEOPLE_SEEN_REDIS_PRE + collectionId, userFinger);
    }

    public void pullStaticInfo(Collection collection) {
        Integer collectionId = collection.getId();
        collection.setTotalBookmarked(Math.toIntExact(stringRedisTemplate.opsForSet().size(RedisKeyConstant.COLLECTION_BOOKMARK_REDIS_PRE + collectionId)));
        collection.setTotalLiked(Math.toIntExact(stringRedisTemplate.opsForSet().size(RedisKeyConstant.COLLECTION_LIKE_REDIS_PRE + collectionId)));
        collection.setTotalPeopleSeen(Math.toIntExact(stringRedisTemplate.opsForHyperLogLog().size((RedisKeyConstant.COLLECTION_TOTAL_PEOPLE_SEEN_REDIS_PRE + collectionId))));
        //处理是否点赞
        //处理是否收藏
        //异步更新数据库
        dealStaticInfo(collection.getId(), collection.getTotalBookmarked(), collection.getTotalLiked(), collection.getTotalPeopleSeen());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "collectionSummary", key = "#userId+'-0'"),
            @CacheEvict(value = "collectionSummary", key = "#userId+'-1'")
    })
    public void dealUserCollectionSummary(Integer userId) {
        collectionMapper.dealUserPublicCollectionSummary(userId);
        collectionMapper.dealUserPrivateCollectionSummary(userId);
    }

    @Cacheable(value = "collectionSummary", key = "#userId+'-'+#isPublic")
    public Integer queryCollectionSummary(Integer userId, Integer isPublic) {
        return collectionMapper.queryCollectionSummary(userId, isPublic);
    }

    public void modifyUserTotalBookmarkCollection(Integer userId, int modify) {
        collectionMapper.modifyUserTotalBookmarkCollection(userId, modify);
    }

    public Integer queryUserTotalBookmarkCollection(Integer userId) {
        return collectionMapper.queryUserTotalBookmarkCollection(userId);
    }

    public Collection getCollection(Integer collectionId) {
        Collection collection = queryCollectionById(collectionId);
        if (collection.getIsPublic() == 0) {
            Map<String, Object> context = AppContext.get();
            if (context != null && context.get(AuthConstant.USER_ID) != null && (int) context.get(AuthConstant.USER_ID) == collection.getUserId()) {
                return collection;
            } else {
                throw new BusinessException(HttpStatus.FORBIDDEN, "禁止查看他人未公开画集");
            }
        }
        return collection;
    }

    @Cacheable("collectionSerchResult")
    public CompletableFuture<List<Collection>> searchCollection(String keyword, String startCreateDate, String endCreateDate, String startUpdateDate, String endUpdateDate, Integer page, Integer pageSize) {
        return collectionSearchUtil.searchCollection(collectionSearchUtil.build(keyword, startCreateDate, endCreateDate, startUpdateDate, endUpdateDate, page, pageSize)).thenApply(this::queryCollectionById);
    }

    public Integer checkUserAuth(Integer isPublic, Integer userId) {
        //是否登陆，是否查看本人画集
        Integer isSelf = 0;
        Map<String, Object> context = AppContext.get();
        if (context != null && context.get(AuthConstant.USER_ID) != null) {
            if ((int) context.get(AuthConstant.USER_ID) == userId) {
                isSelf = 1;
            } else {
                if (isPublic == null || isPublic == 0) {
                    throw new BusinessException(HttpStatus.FORBIDDEN, "禁止查看他人非公开画作");
                }
            }
        } else {
            if (isPublic == null || isPublic == 0) {
                throw new BusinessException(HttpStatus.FORBIDDEN, "禁止查看他人非公开画作");
            }
        }
        return isSelf;
    }

    public List<CollectionDigest> queryUserCollectionNameList(Integer userId, Integer isPublic) {
        List<Integer> collectionIdList = queryUserCollectionNameListFromDb(userId, isPublic);
        return queryCollectionById(collectionIdList).stream().map(CollectionDigest::castByCollection).collect(Collectors.toList());
    }

    @Cacheable(value = "user_collection_digest_list", key = "#userId+'-'+#isPublic")
    public List<Integer> queryUserCollectionNameListFromDb(Integer userId, Integer isPublic) {
        return collectionMapper.queryUserCollection(userId, 0, 500, 1, isPublic, "create_time", "desc");
    }

    @CacheEvict(value = "collections", key = "#collectionId")
    @Transactional
    public Boolean updateCollectionCover(Integer userId, Integer collectionId, List<Integer> illustIdList) {
        checkCollectionAuth(collectionId, userId);
        List<ImageUrl> imageUrlList = illustIdList.stream().map(e -> {
            Illustration illustration = illustrationBizService.queryIllustrationByIdFromDb(e);
            if (illustration != null) {
                return illustration.getImageUrls().get(0);
            }
            return null;
        }).filter(Objects::nonNull).limit(5).collect(Collectors.toList());
        collectionMapper.updateCollectionCover(collectionId, imageUrlList);
        return true;
    }

    public Boolean fixCollectionCover() {
        List<Integer> collections = collectionMapper.queryAllCollectionWithoutCover();
        collections.forEach(e -> {
            try {
                List<ImageUrl> collect = queryCollectionIllust(e, 1, 5).stream().limit(5).map(i -> i.getImageUrls().get(0)).collect(Collectors.toList());
                collectionMapper.updateCollectionCover(e, collect);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        return true;
    }
}
