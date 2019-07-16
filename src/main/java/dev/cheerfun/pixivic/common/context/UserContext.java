package dev.cheerfun.pixivic.common.context;

import dev.cheerfun.pixivic.common.model.User;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-07-13 13:58
 * @description
 */
public class UserContext {
    private static final ThreadLocal<User> USER_CONTEXT = ThreadLocal.withInitial(() -> null);

    /**
     * 获取当前线程下的用户信息
     *
     * @return 用户信息
     */
    public static User get() {
        return USER_CONTEXT.get();
    }

    /**
     * 将信息放入ThreadLocal中
     * @param user
     */
    public static void set(User user) {
        USER_CONTEXT.set(user);
    }

    /**
     * 移除当前用户信息
     */
    public static void remove() {
        USER_CONTEXT.remove();
    }
}
