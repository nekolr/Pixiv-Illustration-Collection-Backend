package dev.cheerfun.pixivic.biz.forum.service;

import dev.cheerfun.pixivic.biz.forum.mapper.SyncAccountMapper;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/5 9:53 PM
 * @description AsyncAccountService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SyncAccountService {
    private final SyncAccountMapper syncAccountMapper;
    private final HttpClient httpClient;
    private String csrfToken = "whAMfP1jjk5qdbX522ACZnjxUiWyIquTqGH0DIYU";

    public void syncToForum() {
        Integer latestSyncUserId = queryLatestSyncUserId();
        while (true) {
            //每次取50个用户
            List<User> users = queryUser(latestSyncUserId);
            if (users.size() == 0) {
                break;
            }
            for (int i = 0; i < users.size(); i++) {
                String result = register(users.get(i));
                if (!"success".equals(result)) {
                    logError(users.get(i), result);
                }
                updateLatestSyncUserId(users.get(i).getId());
            }
        }

    }

    private void updateLatestSyncUserId(Integer userId) {

    }

    private List<User> queryUser(Integer userId) {
        return syncAccountMapper.queryUser(userId);
    }

    private Integer queryLatestSyncUserId() {
        return syncAccountMapper.queryLatestSyncUserId();
    }

    private String register(User user) {
        URI uri = URI.create("https://discuss.pixivic.com/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri).POST(HttpRequest.BodyPublishers.ofString("")).build();
        //  String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        //return body;
        return null;

    }

    private void logError(User user, String result) {
        syncAccountMapper.logError(user.getId(), result);
    }
}
