package dev.cheerfun.pixivic.biz.web.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.admin.dto.IllustDTO;
import dev.cheerfun.pixivic.biz.web.admin.dto.UsersDTO;
import dev.cheerfun.pixivic.biz.web.admin.mapper.AdminMapper;
import dev.cheerfun.pixivic.biz.web.admin.po.*;
import dev.cheerfun.pixivic.biz.web.admin.repository.*;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.util.TranslationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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

    public List<User> queryUsers(UsersDTO usersDTO, Integer page, Integer pageSize, String orderBy, String orderByMode) {
        return adminMapper.queryUsers(usersDTO, (page - 1) * pageSize, pageSize, orderBy, orderByMode);
    }

    public Integer queryUsersTotal(UsersDTO usersDTO, Integer page, Integer pageSize) {
        return adminMapper.queryUsersTotal(usersDTO, (page - 1) * pageSize, pageSize);
    }

    @CacheEvict(value = "illust", key = "#illustDTO.id")
    public void updateIllusts(IllustDTO illustDTO) {
        adminMapper.updateIllusts(illustDTO);
    }

    public void updateUser(UsersDTO usersDTO) {
        adminMapper.updateUser(usersDTO);
    }

    public void banUser(Integer userId) {
        adminMapper.banUser(userId);
    }

    public List<Comment> queryComment(Comment comment, Integer page, Integer pageSize, String orderBy, String orderByMode) {
        return adminMapper.queryComment(comment, page, pageSize, orderBy, orderByMode);
    }

    public Integer queryCommentTotal(Comment comment, Integer page, Integer pageSize) {
        return adminMapper.queryCommentTotal(comment, page, pageSize);
    }

    public Illustration queryIllustrationById(Integer illustId) throws JsonProcessingException {
        Illustration illustration = objectMapper.readValue(objectMapper.writeValueAsString(illustrationBizService.queryIllustrationById(illustId)), Illustration.class);
        illustration.setTitle("【" + translationUtil.translateToChineseByYouDao(illustration.getTitle()) + "】" + illustration.getTitle());
       /* illustration.getTags().forEach(e -> {
            if (e.getTranslatedName() == null || "".equals(e.getTranslatedName())) {
                e.setTranslatedName(translationUtil.translateToChineseByYouDao(e.getName()));
            }
        });*/
        illustration.setCaption(translationUtil.translateToChineseByYouDao(illustration.getCaption()) + "<br />" + illustration.getCaption());
        return illustration;
    }

    //画集管理
    public Page<CollectionPO> queryCollection(CollectionPO collectionPO, Integer page, Integer pageSize, String orderBy, String orderByMode) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderByMode), orderBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        return collectionRepository.findAll(Example.of(collectionPO), pageable);
    }

    public CollectionPO updateCollection(CollectionPO collectionPO) {
        return collectionRepository.save(collectionPO);
    }

    //讨论管理
    public Page<DiscussionPO> queryDiscussion(DiscussionPO discussionPO, Integer page, Integer pageSize, String orderBy, String orderByMode) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderByMode), orderBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        return discussionRepository.findAll(Example.of(discussionPO), pageable);
    }

    public DiscussionPO updateDiscussion(DiscussionPO discussionPO) {
        return discussionRepository.save(discussionPO);
    }

    //板块管理
    public Page<SectionPO> querySection(SectionPO sectionPO, Integer page, Integer pageSize, String orderBy, String orderByMode) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderByMode), orderBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        return sectionRepository.findAll(Example.of(sectionPO), pageable);
    }

    public SectionPO updateSection(SectionPO sectionPO) {
        return sectionRepository.save(sectionPO);
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

    //评论管理
    public Page<CommentPO> queryComment(CommentPO commentPO, Integer page, Integer pageSize, String orderBy, String orderByMode) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderByMode), orderBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        return commentRepository.findAll(Example.of(commentPO), pageable);
    }

    public CommentPO updateComment(CommentPO commentPO) {
        return commentRepository.save(commentPO);
    }

    //@PostConstruct
    public void test() {
        discussionRepository.findAll().forEach(System.out::println);
    }

}
