package com.yunhang.forum.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具类
 */
public class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm");

    /**
     * 获取相对时间描述（例如："2小时前"）
     */
    public static String getRelativeTime(LocalDateTime time) {
        if (time == null) {
            return "未知时间";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(time, now);

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (days < 7) {
            return days + "天前";
        } else {
            return time.format(DATE_FORMATTER);
        }
    }

    /**
     * 获取格式化的时间（例如："2024-12-06 14:30"）
     */
    public static String getFormattedTime(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return time.format(DATE_FORMATTER);
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

