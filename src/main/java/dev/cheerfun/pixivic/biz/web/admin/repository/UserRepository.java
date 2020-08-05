package dev.cheerfun.pixivic.biz.web.admin.repository;

import dev.cheerfun.pixivic.biz.web.admin.po.UserPO;
import org.springframework.data.repository.CrudRepository;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/4 5:26 下午
 * @description UserRepository
 */
public interface UserRepository extends CrudRepository<UserPO, Integer> {
}
