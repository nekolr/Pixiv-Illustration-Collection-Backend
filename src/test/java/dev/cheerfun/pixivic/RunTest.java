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
        System.out.println((int) "a".charAt(0) - 97);
    }

}

class Host {
    public String hostname;
    public Integer port;
    public String username;
    public String password;
    public String fingerprint;
}
