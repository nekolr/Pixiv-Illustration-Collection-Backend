package dev.cheerfun.pixivic.biz.web.admin.repository;

import dev.cheerfun.pixivic.biz.web.admin.po.CollectionPO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<CollectionPO, Integer> {
}
