package dev.cheerfun.pixivic.biz.web.admin.repository;

import dev.cheerfun.pixivic.biz.web.admin.po.DiscussionPO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionRepository extends JpaRepository<DiscussionPO, Integer> {
}
