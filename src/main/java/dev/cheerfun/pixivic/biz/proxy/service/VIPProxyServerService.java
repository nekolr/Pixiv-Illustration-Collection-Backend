package dev.cheerfun.pixivic.biz.proxy.service;

import dev.cheerfun.pixivic.biz.proxy.mapper.VIPProxyServerMapper;
import dev.cheerfun.pixivic.biz.proxy.po.VIPProxyServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/12 2:36 PM
 * @description VIPProxyServerService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VIPProxyServerService {
    private final VIPProxyServerMapper vipProxyServerMapper;
    private List<VIPProxyServer> availableList;
    private Integer availableListSize;
    private final ExecutorService crawlerExecutorService;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
    final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private final HttpClient httpClient;

    @PostConstruct
    public void init() {
        try {
            //获取服务器列表，并且尝试是否可用
            availableList = vipProxyServerMapper.queryAllServer();
            loopCheck();
        } catch (Exception exception) {
            log.error("初始化vip高速服务器服务失败");
        }
    }

    public void loopCheck() {
        crawlerExecutorService.submit(() -> {
            while (true) {
                try {
                    availableList.forEach(e -> {
                        if (e.getServerAddress() != null && !check(e)) {
                            log.error("检测到" + e + "高级会员线路下线");
                            ban(e);
                        }
                    });
                    writeLock.lock();
                    availableList = vipProxyServerMapper.queryAllServer();
                    availableListSize = availableList.size();
                } finally {
                    writeLock.unlock();
                }
                Thread.sleep(1000 * 60 * 5);
            }
        });
    }

    public List<VIPProxyServer> queryAllServer() {
        readLock.lock();
        try {
            return availableList;
        } finally {
            readLock.unlock();
        }
    }

    public Boolean check(VIPProxyServer vipProxyServer) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(vipProxyServer.getServerAddress() + "/test.jpg")).GET().build();
            int statusCode = httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
            if (statusCode == 404) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //ban(vipProxyServer);
        }
        return false;
    }

    public void ban(VIPProxyServer vipProxyServer) {
        vipProxyServerMapper.ban(vipProxyServer.getId());
    }

    public void addServer(String vipProxyServer) {
        writeLock.lock();
        try {
            vipProxyServerMapper.addServer(vipProxyServer);
            availableList = vipProxyServerMapper.queryAllServer();
            availableListSize = availableList.size();
        } finally {
            writeLock.unlock();
        }
    }

}
