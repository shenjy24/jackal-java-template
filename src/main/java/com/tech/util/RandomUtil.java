package com.tech.util;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机工具类
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-07
 */
public class RandomUtil {

    // ---------- 字符集常量 ----------
    private static final String UPPERCASE_ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String LOWERCASE_ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final String LOWERCASE_LETTERS      = "abcdefghijklmnopqrstuvwxyz";

    /** 仅在需要密码学安全时使用，复用同一实例 */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private RandomUtil() {
    }

    // ===========================
    //  数字随机
    // ===========================

    /**
     * 返回 [min, max] 范围内的随机整数
     *
     * @param min 最小值（含）
     * @param max 最大值（含）
     * @return 随机整数
     * @throws IllegalArgumentException min > max 时抛出
     */
    public static int randomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(
                    String.format("min(%d) must be <= max(%d)", min, max));
        }
        // ThreadLocalRandom 线程安全且性能优于 Random
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * 生成指定位数的纯数字字符串（密码学安全）
     * <p>支持位数范围：1 ~ 18</p>
     *
     * @param digits 位数
     * @return 数字字符串，首位不为 0
     * @throws IllegalArgumentException digits < 1 或 digits > 18 时抛出
     */
    public static String randomDigits(int digits) {
        if (digits < 1 || digits > 18) {
            throw new IllegalArgumentException(
                    "digits must be between 1 and 18, got: " + digits);
        }
        // 用 long 避免 int 溢出（digits >= 10 时 int 会越界）
        long minValue = (long) Math.pow(10, digits - 1);
        long maxValue = (long) Math.pow(10, digits) - 1;
        long value = minValue + (long) (SECURE_RANDOM.nextDouble() * (maxValue - minValue + 1));
        return Long.toString(value);
    }

    // ===========================
    //  字母数字随机码
    // ===========================

    /**
     * 生成指定位数的大写字母+数字随机码
     *
     * @param length 长度
     * @return 随机码（大写）
     */
    public static String randomCode(int length) {
        return randomCode(length, true);
    }

    /**
     * 生成指定位数的字母+数字随机码
     *
     * @param length    长度
     * @param uppercase 是否使用大写字母
     * @return 随机码
     */
    public static String randomCode(int length, boolean uppercase) {
        if (length < 1) {
            throw new IllegalArgumentException("length must be >= 1, got: " + length);
        }
        String charset = uppercase ? UPPERCASE_ALPHANUMERIC : LOWERCASE_ALPHANUMERIC;
        return buildRandomString(length, charset, false);
    }

    // ===========================
    //  字母开头随机字符串
    // ===========================

    /**
     * 生成以字母开头、由小写字母和数字组成的随机字符串
     *
     * @param length 长度（须 >= 1）
     * @return 随机字符串
     */
    public static String randomAlphanumeric(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length must be >= 1, got: " + length);
        }
        return buildRandomString(length, LOWERCASE_ALPHANUMERIC, true);
    }

    // ===========================
    //  私有核心逻辑
    // ===========================

    /**
     * 构建随机字符串的核心方法
     *
     * @param length          字符串长度
     * @param charset         可选字符集
     * @param letterFirstChar 首字符是否强制为字母
     * @return 随机字符串
     */
    private static String buildRandomString(int length, String charset, boolean letterFirstChar) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            if (i == 0 && letterFirstChar) {
                // 首位从纯字母字符集中取
                sb.append(LOWERCASE_LETTERS.charAt(rng.nextInt(LOWERCASE_LETTERS.length())));
            } else {
                sb.append(charset.charAt(rng.nextInt(charset.length())));
            }
        }
        return sb.toString();
    }
}
