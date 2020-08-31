package dev.cheerfun.pixivic.biz.web.admin.repository;

import dev.cheerfun.pixivic.biz.web.admin.po.AppVersionInfoPO;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/31 6:52 下午
 * @description AppVersionInfoRepository
 */
public interface AppVersionInfoRepository extends JpaRepository<AppVersionInfoPO, Integer> {
}
