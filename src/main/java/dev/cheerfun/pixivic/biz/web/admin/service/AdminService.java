package dev.cheerfun.pixivic.biz.web.admin.service;

import dev.cheerfun.pixivic.biz.web.admin.dto.IllustDTO;
import dev.cheerfun.pixivic.biz.web.admin.dto.UsersDTO;
import dev.cheerfun.pixivic.biz.web.admin.mapper.AdminMapper;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/24 2:46 下午
 * @description AdminService
 */
//@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminService {
    private final AdminMapper adminMapper;

    public List<User> queryUsers(UsersDTO usersDTO, Integer page, Integer pageSize) {
        return adminMapper.queryUsers(usersDTO, (page - 1) * pageSize, pageSize);

    }

    public Integer queryUsersTotal(UsersDTO usersDTO, Integer page, Integer pageSize) {
        return adminMapper.queryUsersTotal(usersDTO, (page - 1) * pageSize, pageSize);
    }

    public void updateIllusts(IllustDTO illustDTO) {
        adminMapper.updateIllusts(illustDTO);
    }

    public void updateUser(UsersDTO usersDTO) {
        adminMapper.updateUser(usersDTO);
    }

    public void banUser(Integer userId) {
        adminMapper.banUser(userId);
    }
}
