package dev.cheerfun.pixivic.biz.web.app.service;

import dev.cheerfun.pixivic.biz.web.app.mapper.AppMapper;
import dev.cheerfun.pixivic.biz.web.app.po.AppVersionInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/31 6:22 下午
 * @description AppService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AppService {
    private final AppMapper appMapper;

    public AppVersionInfo queryLatest(String version) {
        //输入当前版本并查询是否最新版本，如果有更新就返回对应信息
        AppVersionInfo appVersionInfo = queryLatest();
        if (appVersionInfo != null) {
            if (versionCompare(appVersionInfo.getVersion(), version) == 1) {
                return appVersionInfo;
            }
        }
        return null;
    }

    //查询最新版本信息
    @Cacheable("latestAppVersion")
    public AppVersionInfo queryLatest() {
        return appMapper.queryLatest();
    }

    @Cacheable(value = "latestAppVersion", key = "#page+'-'+#pageSize")
    public List<AppVersionInfo> queryList(Integer page, Integer pageSize) {
        return appMapper.queryList((page - 1) * pageSize, pageSize);
    }

    public int versionCompare(String v1, String v2) {
        int v1Len = StringUtils.countMatches(v1, ".");
        int v2Len = StringUtils.countMatches(v2, ".");

        if (v1Len != v2Len) {
            int count = Math.abs(v1Len - v2Len);
            if (v1Len > v2Len) {
                for (int i = 1; i <= count; i++) {
                    v2 += ".0";
                }
            } else {
                for (int i = 1; i <= count; i++) {
                    v1 += ".0";
                }
            }
        }

        if (v1.equals(v2)) {
            return 0;
        }

        String[] v1Str = StringUtils.split(v1, ".");
        String[] v2Str = StringUtils.split(v2, ".");
        for (int i = 0; i < v1Str.length; i++) {
            String str1 = "", str2 = "";
            for (char c : v1Str[i].toCharArray()) {
                if (Character.isLetter(c)) {
                    int u = c - 'a' + 1;
                    if (u < 10) {
                        str1 += String.valueOf("0" + u);
                    } else {
                        str1 += String.valueOf(u);
                    }
                } else {
                    str1 += String.valueOf(c);
                }
            }
            for (char c : v2Str[i].toCharArray()) {
                if (Character.isLetter(c)) {
                    int u = c - 'a' + 1;
                    if (u < 10) {
                        str2 += String.valueOf("0" + u);
                    } else {
                        str2 += String.valueOf(u);
                    }
                } else {
                    str2 += String.valueOf(c);
                }
            }
            v1Str[i] = "1" + str1;
            v2Str[i] = "1" + str2;

            int num1 = Integer.parseInt(v1Str[i]);
            int num2 = Integer.parseInt(v2Str[i]);

            if (num1 != num2) {
                if (num1 > num2) {
                    return 1;
                } else {
                    return 2;
                }
            }
        }
        return -1;
    }

}
