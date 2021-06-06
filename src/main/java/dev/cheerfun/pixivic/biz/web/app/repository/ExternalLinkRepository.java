package dev.cheerfun.pixivic.biz.web.app.repository;

import dev.cheerfun.pixivic.biz.web.app.po.ExternalLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalLinkRepository extends JpaRepository<ExternalLink, Integer> {
}
