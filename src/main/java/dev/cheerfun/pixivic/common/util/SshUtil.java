package dev.cheerfun.pixivic.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/6/28 2:20 下午
 * @description SCPUtil
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SshUtil {
    final SSHClient ssh = new SSHClient();
    List<Host> hostList;
    @Value("${ssh.configPath}")
    private String path;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        File file = new File(path);
        try {
            hostList = objectMapper.readValue(file, new TypeReference<ArrayList<Host>>() {
            });
        } catch (IOException e) {
            log.info("初始化ssh工具类失败");
            e.printStackTrace();
        }
    }

    public boolean upload(String remoteHost, String localFilePath, String remoteFilePath) throws IOException {
        Optional<Host> hostOptional = hostList.stream().filter(e -> e.hostname.equals(remoteHost)).findFirst();
        if (hostOptional.isPresent()) {
            Host host = hostOptional.get();
            ssh.connect(host.hostname, host.port);
            try {
                ssh.authPassword(host.username, host.password);
                ssh.useCompression();
                ssh.newSCPFileTransfer().upload(localFilePath, remoteFilePath);
            } catch (Exception e) {
                return false;
            } finally {
                ssh.disconnect();
            }
            return true;
        }
        return false;
    }
}

class Host {
    public String hostname;
    public Integer port;
    public String username;
    public String password;
}
