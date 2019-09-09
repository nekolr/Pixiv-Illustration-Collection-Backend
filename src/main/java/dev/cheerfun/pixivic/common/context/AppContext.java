package dev.cheerfun.pixivic.common.context;

import java.util.Map;

/**
 * @author OysterQAQ
 * @version 2.0
 * @date 2019-09-09 13:58
 * @description
 */
public class AppContext {
    private static final ThreadLocal<Map<String,Object>> CONTEXT = ThreadLocal.withInitial(() -> null);

    public static Map<String,Object> get() {
        return CONTEXT.get();
    }

    public static void set(Map<String,Object> map) {
        CONTEXT.set(map);
    }

    public static void remove() {
        CONTEXT.remove();
    }
}
