package dev.cheerfun.pixivic.biz.web.admin.repository;

import dev.cheerfun.pixivic.biz.web.admin.po.CollectionPO;
import org.springframework.data.repository.CrudRepository;

public interface CollectionRepository extends CrudRepository<CollectionPO, Integer> {
}
