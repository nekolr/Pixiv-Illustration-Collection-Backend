package dev.cheerfun.pixivic.biz.web.admin.repository;

import dev.cheerfun.pixivic.biz.web.admin.po.CommentPO;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<CommentPO, Integer> {
}
