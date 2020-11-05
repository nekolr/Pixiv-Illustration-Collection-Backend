package dev.cheerfun.pixivic.biz.analysis.track.mapper;

import dev.cheerfun.pixivic.biz.analysis.track.domain.Track;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//@Mapper
public interface TrackMapper {

    int insert(List<Track> tracksShouldBeStore);
}
