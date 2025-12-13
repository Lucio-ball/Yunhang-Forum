package com.yunhang.forum.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具类
 */
public class DateUtil {

    private static final DateTimeFormatter FULL_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("MM-dd");

    /**
     * 获取友好的时间显示
     * 例如：刚刚、2小时前、3天前、2024-12-01
     */
    public static String getFriendlyTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "未知时间";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "刚刚";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + "分钟前";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + "小时前";
        } else if (seconds < 2592000) { // 30天
            long days = seconds / 86400;
            return days + "天前";
        } else if (dateTime.getYear() == now.getYear()) {
            return dateTime.format(DATE_FORMATTER);
        } else {
            return dateTime.format(FULL_FORMATTER);
        }
    }

    /**
     * 获取完整的时间格式
     */
    public static String getFullTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(FULL_FORMATTER);
    }

    /**
     * 判断是否为今天
     */
    public static boolean isToday(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return dateTime.toLocalDate().equals(now.toLocalDate());
    }
}

