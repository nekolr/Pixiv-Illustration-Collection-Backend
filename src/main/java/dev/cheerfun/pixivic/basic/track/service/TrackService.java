package dev.cheerfun.pixivic.basic.track.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.basic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.basic.track.mapper.TrackMapper;
import dev.cheerfun.pixivic.basic.track.domain.Track;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final static String LOG_POS = ".log";
    private final TrackMapper trackMapper;
    private final ObjectMapper objectMapper;
    private final JWTUtil jwtUtil;

    public void dailyTrackAnalysis() throws IOException {
        System.gc();
        //将昨天日志记录反序列化
        LocalDate day = LocalDate.now().plusDays(-1);
        String log = Files.readString(Paths.get(logPath, day + LOG_POS));
        //解开token 以用户来分组
        List<Track> tracksShouldBeStore = new ArrayList<>();
        List<Track> tracks = objectMapper.readValue(log, new TypeReference<List<Track>>() {
        });
        tracks.forEach(t -> t.setUserId((int) jwtUtil.getAllClaimsFromToken(t.getAuthorization()).get("userId")));
        Map<Integer, List<Track>> groupByUserId = tracks.stream().collect(Collectors.groupingBy(Track::getUserId));
        //进一步针对每个用户以时间来增加断点
        groupByUserId.keySet().forEach(userId -> {
            List<Track> tracksByUserId = groupByUserId.get(userId);
            //以时间划分
            int size = tracksByUserId.size();
            List<Integer> timePoint = new ArrayList<>();
            timePoint.add(0);
            for (int i = 0; i < size; i++) {
                Track curr = tracksByUserId.get(i);
                Track after = tracksByUserId.get(i + 1);
                if (Duration.between(curr.getTimestamp(), after.getTimestamp()).toMinutes() > 10) {
                    tracksShouldBeStore.add(curr);
                    //取末尾元素，则为这次断点的初始index
                    Integer before =timePoint.get(timePoint.size() - 1)+1;
                    //时间段范围再根据行为划分
                    Map<String, List<Track>> map = tracksByUserId.subList(before, i).stream().collect(Collectors.groupingBy(e -> e.getMethod() + ":" + e.getUrl() + ":" + e.getArgs().replaceAll("&page=[0-9]+", "")));
                    map.keySet().forEach(e -> {
                        List<Track> t = map.get(e);
                        tracksShouldBeStore.add(t.get(t.size() - 1));
                    });
                    timePoint.add(i);
                }
            }
        });
        //增加说明
        addDesc(tracksShouldBeStore);
        //持久化
        trackMapper.insert(tracksShouldBeStore);
        System.gc();
    }

    private void addDesc(List<Track> tracksShouldBeStore) {

    }

}
