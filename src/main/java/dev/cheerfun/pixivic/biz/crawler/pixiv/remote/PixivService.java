package dev.cheerfun.pixivic.biz.crawler.pixiv.remote;

import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustsDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PixivService {

    @GET("/v1/user/illusts")
    Call<IllustsDTO> pullArtistIllust(@Query("user_id") Integer artistId, @Query("offset") Integer offset, @Query("type") String type);

}
