package dev.cheerfun.pixivic.common.context;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 13:58
 * @description
 */
public class UserContext {
    private static final ThreadLocal<String> USER_CONTEXT = ThreadLocal.withInitial(() -> null);

    /**
     * 获取当前线程下的用户信息
     *
     * @return 用户信息
     */
    public static String get() {
        return USER_CONTEXT.get();
    }

    /**
     * 将信息放入ThreadLocal中
     * @param userInfo
     */
    public static void set(String userInfo) {
        USER_CONTEXT.set(userInfo);
    }

    /**
     * 移除当前用户信息
     */
    public static void remove() {
        USER_CONTEXT.remove();
    }
}
