package dev.cheerfun.pixivic.biz.web.app.service;

import dev.cheerfun.pixivic.biz.web.admin.po.CommentPO;
import dev.cheerfun.pixivic.biz.web.app.po.ExternalLink;
import dev.cheerfun.pixivic.biz.web.app.repository.ExternalLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/6/6 8:21 PM
 * @description ExternalLinkService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ExternalLinkService {
    private final ExternalLinkRepository externalLinkRepository;

    @Cacheable("externalLinks")
    public List<ExternalLink> queryAllExternalLink() {
        return externalLinkRepository.findAll();
    }

    @Caching(evict = {
            @CacheEvict(value = "externalLinks", allEntries = true)
    })
    public ExternalLink update(ExternalLink externalLink) {
        return externalLinkRepository.save(externalLink);
    }

    @Caching(evict = {
            @CacheEvict(value = "externalLinks", allEntries = true)
    })
    public boolean delete(Integer externalLinkId) {
        externalLinkRepository.deleteById(externalLinkId);
        return true;
    }

    @Caching(evict = {
            @CacheEvict(value = "externalLinks", allEntries = true)
    })
    public ExternalLink add(ExternalLink externalLink) {
        return externalLinkRepository.save(externalLink);
    }

}
