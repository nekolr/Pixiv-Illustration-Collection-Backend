package dev.cheerfun.pixivic.biz.web.cbir.secmapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/26 12:29 AM
 * @description CBIRMapper
 */

public interface CBIRSyncMapper {
    @Select(" SELECT illust_id from illusts where update_time between #{fromDate} and #{toDate}  and total_bookmarks>100 and sanity_level<7 and x_restrict=0")
    List<Integer> queryForSync(String fromDate, String toDate);

    @Insert("replace into cbir_sync_log (sync_date, is_finish) values (#{date},#{isFinish})")
    Integer updateSyncLog(String date, int isFinish);
}
