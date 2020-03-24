package dev.cheerfun.pixivic.biz.notify.util;

import dev.cheerfun.pixivic.biz.notify.mapper.NotifyMapper;
import dev.cheerfun.pixivic.biz.notify.po.NotifyBanSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/24 4:09 下午
 * @description UserSettingUtil
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserSettingUtil {
    private final NotifyMapper notifyMapper;

    @Cacheable("userNotifySetting")
    public Boolean checkUserSetting(Integer ownerId, String objectType) {
        NotifyBanSetting notifyBanSetting = notifyMapper.queryUserBanSetting(ownerId);
        return notifyBanSetting == null || !notifyBanSetting.getBanNotifyActionType().contains(objectType);
    }
}
