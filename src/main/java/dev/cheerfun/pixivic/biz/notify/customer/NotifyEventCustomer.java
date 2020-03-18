package dev.cheerfun.pixivic.biz.notify.customer;

import dev.cheerfun.pixivic.biz.notify.mapper.NotifyMapper;
import dev.cheerfun.pixivic.biz.notify.po.NotifyEvent;
import dev.cheerfun.pixivic.biz.notify.po.NotifySettingConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class NotifyEventCustomer {
    private final NotifyMapper notifyMapper;
    private Map<String, NotifySettingConfig> notifySettingMap;

    @PostConstruct
    void init() {
        notifySettingMap = notifyMapper.queryNotifySettingConfig().stream().collect(Collectors.toMap(e -> e.getObjectType() + e.getObjectRelationship() + e.getAction(), e -> e));
    }

    protected abstract void process(NotifyEvent notifyEvent);

}
