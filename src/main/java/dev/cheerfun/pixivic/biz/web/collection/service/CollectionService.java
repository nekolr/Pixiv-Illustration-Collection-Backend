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
import org.springframework.cache.CacheManager;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static dev.cheerfun.pixivic.common.constant.RedisKeyConstant.COLLECTION_REORDER_LOCK;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/5/2 7:55 ??????
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
    private final CacheManager cacheManager;

    @Caching(evict = {
            @CacheEvict(value = "user_collection_digest_list", key = "#userId+'-0'"),
            @CacheEvict(value = "user_collection_digest_list", key = "#userId+'-null'"),
            @CacheEvict(value = "user_collection_digest_list", key = "#userId+'-1'"),
            @CacheEvict("publicCollectionCount")
    })
    public Integer createCollection(Integer userId, Collection collection) {
        collection.setCreateTime(LocalDateTime.now());
        //????????????
        collection.setUserId(userId);
        collectionMapper.createCollection(collection);
        //????????????
        dealUserCollectionSummary(userId);
        //?????????tag??????
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
            throw new BusinessException(HttpStatus.BAD_REQUEST, "??????????????????");
        }
        //??????collectionId??????????????????
        checkCollectionAuth(collection.getId(), userId);
        collectionMapper.updateCollection(userId, collection);
        //????????????????????????
        //??????????????????????????????????????????
        //????????????
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
        //???????????????????????????????????????
        if (collectionMapper.deleteCollection(userId, collectionId) == 1) {
            //mysql??????
            collectionMapper.deleteCollectionBookmark(collectionId);
            //????????????
            dealUserCollectionSummary(userId);
            //redis??????
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
        //??????collectionId??????????????????
        checkCollectionAuth(collectionId, userId);
        Collection collection = queryCollectionByIdFromDb(collectionId);
        Integer index = collection.getIllustCount();
        //??????
        int sum = 0;
        for (Integer illustrationId : illustrationIds) {
            try {
                collectionMapper.addIllustrationToCollection(collectionId, illustrationId, ++index);
                sum++;
            } catch (DuplicateKeyException e) {
                log.info("?????????" + illustrationId + "????????????" + collectionId + "???????????????");
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
        //??????collectionId??????????????????
        checkCollectionAuth(collectionId, userId);
        //??????
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
        throw new BusinessException(HttpStatus.FORBIDDEN, "???????????????????????????");

    }

    public boolean checkCollectionUpdateStatus(Integer collectionId) {
        return stringRedisTemplate.opsForValue().setIfAbsent(COLLECTION_REORDER_LOCK + collectionId, "Y", 10, TimeUnit.SECONDS);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateIllustrationOrder(Integer collectionId, List<Integer> illustIdList, Integer userId) {
        //??????collectionId??????????????????
        checkCollectionAuth(collectionId, userId);
        //??????0???size???????????????
        collectionMapper.deleteIllustrationByIndexFromCollection(collectionId, illustIdList.size());
        //????????????
        collectionMapper.insertIllustrationByIndexToCollection(collectionId, illustIdList);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateIllustrationOrder(Integer collectionId, UpdateIllustrationOrderDTO updateIllustrationOrderDTO, Integer userId) {
        long start = System.currentTimeMillis();
        //log.info(String.valueOf(start));
        //??????collectionId??????????????????
        checkCollectionAuth(collectionId, userId);
        //????????????illust???????????????????????????????????????????????? ?????? ????????????
        //??????????????????????????????????????????
        Integer illustrationOrder = queryIllustrationOrder(collectionId, updateIllustrationOrderDTO.getReOrderIllustrationId());
        if (illustrationOrder == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "?????????????????????");
        }
        //???????????????cas
        long now = System.currentTimeMillis();
        while (System.currentTimeMillis() - now < 10 * 1000) {
            //????????????
            if (checkCollectionUpdateStatus(collectionId)) {
                continue;
            }
            Integer upperIndex;
            Integer lowerIndex;
            Integer originalIndex;
            Integer resultIndex;
            //????????????
            try {
                originalIndex = queryIllustrationOrder(collectionId, updateIllustrationOrderDTO.getReOrderIllustrationId());
                upperIndex = queryIllustrationOrder(collectionId, updateIllustrationOrderDTO.getUpIllustrationId());
                if (upperIndex == null) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "?????????????????????");
                }
                //?????????
                if (updateIllustrationOrderDTO.getLowIllustrationId() == null) {
                    //????????????????????????+1w
                    resultIndex = upperIndex + 10000;
                } else {
                    //????????????index
                    lowerIndex = queryIllustrationOrder(collectionId, updateIllustrationOrderDTO.getLowIllustrationId());
                    if (lowerIndex == null) {
                        throw new BusinessException(HttpStatus.BAD_REQUEST, "?????????????????????");
                    }
                    resultIndex = (upperIndex + lowerIndex) / 2;
                }
                if (resultIndex.compareTo(originalIndex) != 0) {
                    //??????
                    collectionMapper.updateIllustrationOrder(collectionId, updateIllustrationOrderDTO.getReOrderIllustrationId(), resultIndex);
                    //???????????????????????????
                    collectionMapper.incrIllustrationInsertFactor(collectionId, updateIllustrationOrderDTO.getUpIllustrationId());
                    Integer insertFactor = collectionMapper.queryIllustrationInsertFactor(collectionId, updateIllustrationOrderDTO.getUpIllustrationId());
                    //??????????????????????????????
                    if (insertFactor >= 10) {
                        //?????????????????????????????????????????????????????????0
                        collectionMapper.reOrderIllustration(collectionId);
                    }
                }
                log.info("?????????" + (System.currentTimeMillis() - start));
                return true;
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new BusinessException(HttpStatus.EXPECTATION_FAILED, "????????????");
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
        //???????????????????????? ??????isPublic ??????user?????????
        Collection collection = queryCollectionById(collectionId);
        if (collection == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "??????????????????");
        }
        if (collection.getIsPublic() == 0) {
            Map<String, Object> context = AppContext.get();
            if (context != null && context.get(AuthConstant.USER_ID) != null) {
                if ((int) context.get(AuthConstant.USER_ID) != collection.getUserId()) {
                    throw new BusinessException(HttpStatus.FORBIDDEN, "???????????????");
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

    public List<CollectionTag> autocompleteCollectionTag(String keyword) throws ExecutionException, InterruptedException {
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
        //??????????????????
        //??????????????????
        //?????????????????????
        dealStaticInfo(collection.getId(), collection.getTotalBookmarked(), collection.getTotalLiked(), collection.getTotalPeopleSeen());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "collectionSummary", key = "#userId+'-0'"),
            @CacheEvict(value = "collectionSummary", key = "#userId+'-1'"),
            @CacheEvict(value = "collectionSummary", key = "#userId+'-null'"),
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
        if (collection != null && collection.getIsPublic() == 0) {
            Map<String, Object> context = AppContext.get();
            if (context != null && context.get(AuthConstant.USER_ID) != null && (int) context.get(AuthConstant.USER_ID) == collection.getUserId()) {
                return collection;
            } else {
                throw new BusinessException(HttpStatus.FORBIDDEN, "?????????????????????????????????");
            }
        }
        return collection;
    }

    public List<Collection> searchCollection(String keyword, String startCreateDate, String endCreateDate, String startUpdateDate, String endUpdateDate, Integer page, Integer pageSize) throws ExecutionException, InterruptedException {
        return queryCollectionById(collectionSearchUtil.searchCollection(collectionSearchUtil.build(keyword, startCreateDate, endCreateDate, startUpdateDate, endUpdateDate, page, pageSize)));
    }

    public Integer checkUserAuth(Integer isPublic, Integer userId) {
        //???????????????????????????????????????
        Integer isSelf = 0;
        Map<String, Object> context = AppContext.get();
        if (context != null && context.get(AuthConstant.USER_ID) != null) {
            if ((int) context.get(AuthConstant.USER_ID) == userId) {
                isSelf = 1;
            } else {
                if (isPublic == null || isPublic == 0) {
                    throw new BusinessException(HttpStatus.FORBIDDEN, "?????????????????????????????????");
                }
            }
        } else {
            if (isPublic == null || isPublic == 0) {
                throw new BusinessException(HttpStatus.FORBIDDEN, "?????????????????????????????????");
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

    public void evictCacheByUser(Integer userId) {
        queryUserCollectionNameListFromDb(userId, null).stream().parallel().forEach(e -> cacheManager.getCache("collections").evictIfPresent(e));
    }
}
