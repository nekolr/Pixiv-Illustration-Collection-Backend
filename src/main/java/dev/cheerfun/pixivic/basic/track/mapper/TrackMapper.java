package dev.cheerfun.pixivic.basic.track.mapper;

import dev.cheerfun.pixivic.basic.track.domain.Track;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TrackMapper {

    int insert(List<Track> tracksShouldBeStore);
}
