package com.tech.util;

import com.tech.common.constant.Constants;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 时间工具类
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-06
 */
public class TimeUtil {
    // ── 格式字符串 ──────────────────────────────────────────
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATETIME_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String PATTERN_COMPACT = "yyMMddHHmmssSSS";

    // ── 预建格式化器（线程安全，复用） ─────────────────────
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern(PATTERN_DATE);
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern(PATTERN_DATETIME);
    private static final DateTimeFormatter FMT_DATETIME_UTC = DateTimeFormatter.ofPattern(PATTERN_DATETIME_UTC);
    private static final DateTimeFormatter FMT_COMPACT = DateTimeFormatter.ofPattern(PATTERN_COMPACT);

    // 统一时区
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private TimeUtil() {
    }

    // ===========================
    //  格式化：对象 → 字符串
    // ===========================

    /**
     * LocalDateTime → "yyMMddHHmmssSSS"
     */
    public static String formatCompact(LocalDateTime dateTime) {
        return FMT_COMPACT.format(dateTime);
    }

    /**
     * 毫秒时间戳 → "yyMMddHHmmssSSS"
     */
    public static String formatCompact(long millis) {
        return formatCompact(fromMillis(millis));
    }

    /**
     * LocalDate → "yyyy-MM-dd"
     */
    public static String formatDate(LocalDate date) {
        return FMT_DATE.format(date);
    }

    /**
     * LocalDateTime → "yyyy-MM-dd HH:mm:ss"
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return FMT_DATETIME.format(dateTime);
    }

    /**
     * 毫秒时间戳 → "yyyy-MM-dd"
     */
    public static String formatDate(long millis) {
        return formatDate(fromMillis(millis).toLocalDate());
    }

    /**
     * 毫秒时间戳 → "yyyy-MM-dd HH:mm:ss"
     */
    public static String formatDateTime(long millis) {
        return formatDateTime(fromMillis(millis));
    }

    /**
     * UTC 当前时间 → "yyyy-MM-dd'T'HH:mm:ss'Z'"
     */
    public static String formatUtcNow() {
        return LocalDateTime.now(ZoneOffset.UTC).format(FMT_DATETIME_UTC);
    }

    // ===========================
    //  解析：字符串 → 对象
    // ===========================

    /**
     * "yyyy-MM-dd" → LocalDate
     */
    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, FMT_DATE);
    }

    /**
     * "yyyy-MM-dd HH:mm:ss" → LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, FMT_DATETIME);
    }

    /**
     * "yyyy-MM-dd'T'HH:mm:ss'Z'"（UTC）→ LocalDateTime（UTC）
     */
    public static LocalDateTime parseUtcDateTime(String utcTime) {
        return LocalDateTime.parse(utcTime, FMT_DATETIME_UTC);
    }

    // ===========================
    //  转换：→ 毫秒时间戳
    // ===========================

    /**
     * "yyyy-MM-dd" → 毫秒时间戳（当天 00:00:00）
     */
    public static long toMillisFromDate(String date) {
        return toMillis(parseDate(date));
    }

    /**
     * "yyyy-MM-dd HH:mm:ss" → 毫秒时间戳
     */
    public static long toMillisFromDateTime(String dateTime) {
        return toMillis(parseDateTime(dateTime));
    }

    /**
     * UTC 时间字符串 → 毫秒时间戳
     */
    public static long toMillisFromUtc(String utcTime) {
        return parseUtcDateTime(utcTime).toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    /**
     * LocalDate → 毫秒时间戳（当天 00:00:00）
     */
    public static long toMillis(LocalDate date) {
        return date.atStartOfDay(ZONE).toInstant().toEpochMilli();
    }

    /**
     * LocalDateTime → 毫秒时间戳
     */
    public static long toMillis(LocalDateTime dateTime) {
        return dateTime.atZone(ZONE).toInstant().toEpochMilli();
    }

    /**
     * 毫秒 → 秒（int）
     */
    public static int toSeconds(long millis) {
        return (int) (millis / 1000);
    }

    // ===========================
    //  转换：毫秒时间戳 → 对象
    // ===========================

    /**
     * 毫秒时间戳 → LocalDateTime
     */
    public static LocalDateTime fromMillis(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZONE);
    }

    // ===========================
    //  当前时间快捷方法
    // ===========================

    /**
     * 当前日期
     */
    public static LocalDate currentDate() {
        return LocalDate.now(ZONE);
    }

    /**
     * 当前时间
     */
    public static LocalDateTime currentDateTime() {
        return LocalDateTime.now(ZONE);
    }

    /**
     * 当前日期字符串 "yyyy-MM-dd"
     */
    public static String currentDateStr() {
        return formatDate(LocalDate.now(ZONE));
    }

    /**
     * 当前时间字符串 "yyyy-MM-dd HH:mm:ss"
     */
    public static String currentDateTimeStr() {
        return formatDateTime(LocalDateTime.now(ZONE));
    }

    /**
     * 当前秒级时间戳
     */
    public static int currentSeconds() {
        return toSeconds(System.currentTimeMillis());
    }

    /**
     * 当前毫秒时间戳（Timestamp）
     */
    public static Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    // ===========================
    //  业务工具方法
    // ===========================

    /**
     * Token 过期时间
     */
    public static Timestamp tokenExpireTime() {
        return new Timestamp(System.currentTimeMillis() + Constants.TOKEN_EXPIRED_MS);
    }

    /**
     * 某天开始时间 00:00:00
     */
    public static Timestamp dayStart(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        LocalDateTime start = timestamp.toLocalDateTime().toLocalDate().atStartOfDay();
        return Timestamp.valueOf(start);
    }

    /**
     * 某天结束时间 23:59:59
     */
    public static Timestamp dayEnd(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        LocalDateTime end = timestamp.toLocalDateTime().toLocalDate().atTime(LocalTime.MAX);
        return Timestamp.valueOf(end);
    }

    /**
     * 从指定日期加 duration 后的结束时间 23:59:59
     */
    public static Timestamp nextPeriodEnd(Timestamp startDay, int durationValue, ChronoUnit unit) {
        LocalDate nextDate = startDay.toLocalDateTime().toLocalDate().plus(durationValue, unit);
        LocalDateTime nextEnd = nextDate.atTime(LocalTime.MAX);
        return Timestamp.valueOf(nextEnd);
    }

    // ===========================
    //  main
    // ===========================

    public static void main(String[] args) {
        System.out.println(formatCompact(LocalDateTime.now()));
        System.out.println(formatDate(LocalDate.now()));
        System.out.println(formatDateTime(LocalDateTime.now()));
        System.out.println(formatUtcNow());

        long now = System.currentTimeMillis();
        System.out.println(formatDate(now));
        System.out.println(formatDateTime(now));
        System.out.println(fromMillis(now));
    }
}
