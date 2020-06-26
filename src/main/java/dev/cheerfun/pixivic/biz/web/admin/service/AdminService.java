package dev.cheerfun.pixivic.biz.web.admin.service;

import dev.cheerfun.pixivic.biz.web.admin.mapper.AdminMapper;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

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
    private List<String> keyList;

    @PostConstruct
    public void init() {
        log.info("开始初始化管理员key列表");
        //初始化固定token
        keyList = adminMapper.queryAllAdminKey();
        log.info("初始化管理员key列表完毕");
    }

    public boolean validateKey(String token) {
        return keyList.contains(token);
    }

//    public List<User> queryUsers(UsersDTO usersDTO, Integer page, Integer pageSize) {
//        return adminMapper.queryUsers(usersDTO, (page - 1) * pageSize, pageSize);
//
//    }
//
//    public Integer queryUsersTotal(UsersDTO usersDTO, Integer page, Integer pageSize) {
//        return adminMapper.queryUsersTotal(usersDTO, (page - 1) * pageSize, pageSize);
//    }
//
//    public void updateIllusts(IllustDTO illustDTO) {
//        adminMapper.updateIllusts(illustDTO);
//    }
//
//    public void updateUser(UsersDTO usersDTO) {
//        adminMapper.updateUser(usersDTO);
//    }

    public void banUser(Integer userId) {
        adminMapper.banUser(userId);
    }
}
