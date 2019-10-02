package dev.cheerfun.pixivic.track.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.track.model.Track;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/16 9:40
 * @description TrackService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TrackService {
    @Value("${apiLog.path")
    private String logPath;
    private final static String LOG_POS=".log";
    private final ObjectMapper objectMapper;
    private final JWTUtil jwtUtil;

    public void dailyTrackAnalysis() throws IOException {
        System.gc();
        //将昨天日志记录反序列化
        LocalDate day = LocalDate.now().plusDays(-1);
        String log = Files.readString(Paths.get(logPath, day+LOG_POS));
      //解开token 以用户来分组
        List<Track> tracks = objectMapper.readValue(log, new TypeReference<List<Track>>() {
        });
        Map<Object, List<Track>> groupByUserId = tracks.stream().collect(Collectors.groupingBy(e -> jwtUtil.getAllClaimsFromToken(e.getAuthorization()).get("userId")));
        //再以时间分组
        //groupByUserId.forEach();
        //再以url路径以及参数
        //找到断点，加入书签
    }
}
