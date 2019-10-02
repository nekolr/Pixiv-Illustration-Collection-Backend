package dev.cheerfun.pixivic.track.mapper;

import dev.cheerfun.pixivic.track.model.Track;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TrackMapper {

    int insert(List<Track> tracksShouldBeStore);
}
