package dev.cheerfun.pixivic.common.constant;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/20 下午2:20
 * @description RedisKeyConstant
 */
public class RedisKeyConstant {
    public final static String BOOKMARK_REDIS_PRE = "u:b:";
    public final static String BOOKMARK_COUNT_MAP_REDIS_PRE = "u:bcm";
    public final static String USER_FOLLOW_REDIS_PRE = "u:f:";
    public final static String ARTIST_FOLLOW_REDIS_PRE = "a:f:";
    public final static String NOTIFY_EVENT_STREAM_KEY = "n:e";
    public final static String NOTIFY_EVENT_STREAM_GROUP = "common";
    public final static String LIKE_REDIS_PRE = "u:l:c:";
    public final static String LIKE_COUNT_MAP_REDIS_PRE = "c:lcm";//+appType:appId
    public final static String ILLUST_BROWSING_HISTORY_REDIS_PRE = "u:h:i:";
    public final static String ARTIST_LATEST_ILLUSTS_PULL_FLAG = "a:l:p:f:";
    public final static String COLLECTION_REORDER_LOCK = "crl:";
    public final static String ILLUST_NOT_IN_PIXIV = "c:nip";
    public final static String ARTIST_NOT_IN_PIXIV = "a:nip";
    public final static String COLLECTION_BOOKMARK_REDIS_PRE = "c:b:";
    public final static String COLLECTION_LIKE_REDIS_PRE = "c:l:";
    public final static String COLLECTION_TOTAL_VIEW_REDIS_PRE = "c:tv:";
    public final static String COLLECTION_TOTAL_PEOPLE_SEEN_REDIS_PRE = "c:tps:";
    public final static String ACCOUNT_BAN_COUNT_MAP = "a:bcm";
    public final static String ACCOUNT_BAN_SET = "a:bs";
    public final static String USER_RECOMMEND_BOOKMARK_ILLUST = "u:r:b:i:";
    public final static String USER_RECOMMEND_VIEW_ILLUST = "u:r:v:i:";
    public final static String USER_RECOMMEND_ARTIST = "u:r:a:";
}
