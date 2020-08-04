package dev.cheerfun.pixivic.biz.web.admin.repository;

import dev.cheerfun.pixivic.biz.web.admin.po.CommentPO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentPO, Integer> {
}
