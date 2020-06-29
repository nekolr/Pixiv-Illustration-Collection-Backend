package dev.cheerfun.pixivic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class RunTest {

    int i = 0;
    volatile int j = 0;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        final SSHClient ssh = new SSHClient();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        List<Host> hostList = objectMapper.readValue(new File("/Users/oysterqaq/PIC/ssh.conf"), new TypeReference<ArrayList<Host>>() {
        });
        Optional<Host> hostOptional = hostList.stream().filter(e -> e.hostname.equals("static")).findFirst();
        if (hostOptional.isPresent()) {
            Host host = hostOptional.get();
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(host.hostname, host.port);
            try {
                ssh.authPassword(host.username, host.password);
                ssh.useCompression();
                ssh.newSCPFileTransfer().upload("/Users/oysterqaq/PIC/ssh.conf", "/root");
            } catch (Exception e) {

            } finally {
                ssh.disconnect();
            }

        }

    }

}

class Host {
    public String hostname;
    public Integer port;
    public String username;
    public String password;
    public String fingerprint;
}
