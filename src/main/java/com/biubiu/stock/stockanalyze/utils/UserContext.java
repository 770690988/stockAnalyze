package com.biubiu.stock.stockanalyze.utils;

/**
 * @Author biubiu
 * @Description 用户上下文数据
 * @Date 2026/5/3 14:37
 */
public class UserContext {
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public static void set(Long userId) {
        currentUserId.set(userId);
    }

    public static Long get() {
        return currentUserId.get();
    }

    public static void clear() {
        currentUserId.remove();
    }
}
