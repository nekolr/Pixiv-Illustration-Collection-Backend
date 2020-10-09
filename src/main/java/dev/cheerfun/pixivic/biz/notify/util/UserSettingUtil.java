package dev.cheerfun.pixivic.biz.notify.util;

import dev.cheerfun.pixivic.biz.notify.mapper.NotifyMapper;
import dev.cheerfun.pixivic.biz.notify.po.NotifyBanSetting;
import dev.cheerfun.pixivic.biz.notify.service.NotifyRemindService;
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
    private final NotifyRemindService notifyRemindService;

    @Cacheable("userNotifySetting")
    public Boolean checkUserSetting(Integer ownerId, String objectType) {
        NotifyBanSetting notifyBanSetting = notifyRemindService.queryUserBanSetting(ownerId);
        return notifyBanSetting == null || !notifyBanSetting.getBanObjectType().contains(objectType);
    }
}
