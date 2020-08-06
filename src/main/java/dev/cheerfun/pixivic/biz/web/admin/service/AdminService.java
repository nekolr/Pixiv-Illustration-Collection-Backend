package dev.cheerfun.pixivic.biz.web.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.admin.dto.IllustDTO;
import dev.cheerfun.pixivic.biz.web.admin.mapper.AdminMapper;
import dev.cheerfun.pixivic.biz.web.admin.po.*;
import dev.cheerfun.pixivic.biz.web.admin.repository.*;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.util.TranslationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/24 2:46 下午
 * @description AdminService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AdminService {
    private final AdminMapper adminMapper;
    private final TranslationUtil translationUtil;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final DiscussionRepository discussionRepository;
    private final SectionRepository sectionRepository;
    private final CommentRepository commentRepository;
    private final CollectionRepository collectionRepository;
    private final IllustrationBizService illustrationBizService;
    private List<String> keyList;

    @PostConstruct
    public void init() {
        log.info("开始初始化管理员key列表");
        //初始化固定token
        try {
            keyList = adminMapper.queryAllAdminKey();
        } catch (Exception e) {
            log.error("初始化管理员key列表失败");
        }
        log.info("初始化管理员key列表完毕");
    }

    public boolean validateKey(String token) {
        return keyList.contains(token);
    }

    @CacheEvict(value = "illust", key = "#illustDTO.id")
    public void updateIllusts(IllustDTO illustDTO) {
        adminMapper.updateIllusts(illustDTO);
    }

    public Illustration queryIllustrationById(Integer illustId) throws JsonProcessingException {
        Illustration illustration = objectMapper.readValue(objectMapper.writeValueAsString(illustrationBizService.queryIllustrationById(illustId)), Illustration.class);
        illustration.setTitle("【" + translationUtil.translateToChineseByYouDao(illustration.getTitle()) + "】" + illustration.getTitle());
        illustration.getTags().forEach(e -> {
            if (e.getTranslatedName() == null || "".equals(e.getTranslatedName())) {
                e.setTranslatedName(translationUtil.translateToChineseByYouDao(e.getName()));
            }
        });
        illustration.setCaption(translationUtil.translateToChineseByYouDao(illustration.getCaption()) + "<br />" + illustration.getCaption());
        return illustration;
    }

    //画集管理
    public Page<CollectionPO> queryCollection(CollectionPO collectionPO, Integer page, Integer pageSize, String orderBy, String orderByMode) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderByMode), orderBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        return collectionRepository.findAll(Example.of(collectionPO), pageable);
    }

    @CacheEvict(value = "collections", key = "#collectionPO.id")
    public CollectionPO updateCollection(CollectionPO collectionPO) {
        return collectionRepository.save(collectionPO);
    }

    //讨论管理
    public Page<DiscussionPO> queryDiscussion(DiscussionPO discussionPO, Integer page, Integer pageSize, String orderBy, String orderByMode) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderByMode), orderBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        return discussionRepository.findAll(Example.of(discussionPO), pageable);
    }

    @CacheEvict(value = "discussions", key = "#discussionPO.id")
    public DiscussionPO updateDiscussion(DiscussionPO discussionPO) {
        return discussionRepository.save(discussionPO);
    }

    @CacheEvict(value = "sectionDiscussionCount", key = "#discussionPO.sectionId")
    public void deleteDiscussion(DiscussionPO discussionPO) {
        discussionRepository.deleteById(discussionPO.getId());
    }

    //板块管理
    public Page<SectionPO> querySection(SectionPO sectionPO, Integer page, Integer pageSize, String orderBy, String orderByMode) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderByMode), orderBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        return sectionRepository.findAll(Example.of(sectionPO), pageable);
    }

    @CacheEvict(value = "section", allEntries = true)
    public SectionPO addSection(SectionPO sectionPO) {
        return sectionRepository.save(sectionPO);
    }

    @CacheEvict(value = "section", allEntries = true)
    public SectionPO updateSection(SectionPO sectionPO) {
        return sectionRepository.save(sectionPO);
    }

    @CacheEvict(value = "section", allEntries = true)
    public void deleteSection(SectionPO sectionPO) {
        sectionRepository.deleteById(sectionPO.getId());
    }

    //用户管理
    public Page<UserPO> queryUsers(UserPO userPO, Integer page, Integer pageSize, String orderBy, String orderByMode) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderByMode), orderBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        return userRepository.findAll(Example.of(userPO), pageable);
    }

    public UserPO updateUser(UserPO userPO) {
        return userRepository.save(userPO);
    }

    public void deleteUser(UserPO userPO) {
        userRepository.delete(userPO);
    }

    //评论管理
    public Page<CommentPO> queryComment(CommentPO commentPO, Integer page, Integer pageSize, String orderBy, String orderByMode) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderByMode), orderBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        return commentRepository.findAll(Example.of(commentPO), pageable);
    }

    @Caching(evict = {
            @CacheEvict(value = "comments", key = "#commentPO.appType+#commentPO.appId"),
            @CacheEvict(value = "topCommentsCount", key = "#commentPO.appType+#commentPO.appId")
    })
    public CommentPO updateComment(CommentPO commentPO) {
        return commentRepository.save(commentPO);
    }

    @Caching(evict = {
            @CacheEvict(value = "comments", key = "#commentPO.appType+#commentPO.appId"),
            @CacheEvict(value = "topCommentsCount", key = "#commentPO.appType+#commentPO.appId")
    })
    public void deleteComment(CommentPO commentPO) {
        commentRepository.deleteById(commentPO.getId());
    }

    //@PostConstruct
    public void test() {
        discussionRepository.findAll().forEach(System.out::println);
    }

}
