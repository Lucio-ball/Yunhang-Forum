package com.yunhang.forum.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具类
 */
public class DateUtil {

    private static final DateTimeFormatter DATE_TIME_SECONDS_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm");

    /**
     * 返回当前时间的标准格式字符串。
     */
    public static String now() {
        return LocalDateTime.now().format(DATE_TIME_SECONDS_FORMATTER);
    }

    /**
     * 格式化时间（精确到秒）。
     */
    public static String format(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return time.format(DATE_TIME_SECONDS_FORMATTER);
    }

    /**
     * 解析标准时间字符串（yyyy-MM-dd HH:mm:ss）。
     */
    public static LocalDateTime parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(text.trim(), DATE_TIME_SECONDS_FORMATTER);
    }

    /**
     * 获取相对时间描述（例如："2小时前"）
     */
    public static String getRelativeTime(LocalDateTime time) {
        if (time == null) {
            return "未知时间";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(time, now);

        // 未来时间：直接按日期展示，避免出现负数 “-5 分钟前”
        if (duration.isNegative()) {
            return time.toLocalDate().format(DATE_ONLY_FORMATTER);
        }

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        // long days = duration.toDays();

        if (minutes < 1) {
            return "刚刚";
        }
        if (minutes < 60) {
            return minutes + " 分钟前";
        }

        // 昨天：兼顾跨日但很近的场景。
        // 规则：若确实是昨天，且已过去 >= 6 小时，则显示“昨天 HH:mm”；否则仍显示“X 小时前”。
        if (time.toLocalDate().equals(now.toLocalDate().minusDays(1))) {
            if (hours >= 6) {
                return "昨天 " + time.format(TIME_FORMATTER);
            }
            return hours + " 小时前";
        }

        if (hours < 24) {
            return hours + " 小时前";
        }

        // 其他：显示日期
        return time.toLocalDate().format(DATE_ONLY_FORMATTER);
    }

    /**
     * 获取格式化的时间（例如："2024-12-06 14:30"）
     */
    public static String getFormattedTime(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return time.format(DATE_TIME_SECONDS_FORMATTER);
    }

    /**
     * 获取时间（例如："14:30"）
     */
    public static String getTimeOnly(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return time.format(TIME_FORMATTER);
    }
}

