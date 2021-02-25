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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

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
    private List<VIPProxyServer> availableServerList;
    private List<VIPProxyServer> serverList;
    private final ExecutorService crawlerExecutorService;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
    final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private final HttpClient httpClient;

    @PostConstruct
    public void init() {
        try {
            log.info("开始初始化高速服务器列表服务");
            //获取服务器列表，并且尝试是否可用
            serverList = vipProxyServerMapper.queryAllServer();
            availableServerList = new ArrayList<>(serverList);
            loopCheck();
        } catch (Exception exception) {
            log.error("初始化高速服务器列表服务失败");
        }
        log.info("初始化高速服务器列表服务成功");

    }

    public void loopCheck() {
        crawlerExecutorService.submit(() -> {
            while (true) {
                log.info("开始周期性检查高级会员线路可用性");
                try {
                    List<VIPProxyServer> tempList = serverList.stream().parallel().filter(e -> {
                        if (!check(e)) {
                            log.error("检测到" + e + "高级会员线路下线");
                            //ban(e);
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    writeLock.lock();
                    availableServerList = tempList;
                } finally {
                    writeLock.unlock();
                }
                log.info("检查高级会员线路可用性完成");
                Thread.sleep(1000 * 60 * 5);
            }
        });
    }

    public List<VIPProxyServer> queryAllServer() {
        readLock.lock();
        try {
            return availableServerList;
        } finally {
            readLock.unlock();
        }
    }

    public Boolean check(VIPProxyServer vipProxyServer) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(vipProxyServer.getServerAddress() + "/test.jpg")).GET().build();
            httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void ban(VIPProxyServer vipProxyServer) {
        vipProxyServerMapper.ban(vipProxyServer.getId());
    }

    public void addServer(String vipProxyServer) {
        writeLock.lock();
        try {
            vipProxyServerMapper.addServer(vipProxyServer);
            serverList = vipProxyServerMapper.queryAllServer();
        } finally {
            writeLock.unlock();
        }
    }

}
