package com.yunhang.forum.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Date/time utility using modern java.time API only.
 * <p>
 * - No usage of java.util.Date or SimpleDateFormat.
 * - Provides formatting, parsing, and human-friendly relative time.
 * - All methods are static; class is non-instantiable.
 */
public final class DateUtil {

    // Standard formatter: yyyy-MM-dd HH:mm:ss
    private static final DateTimeFormatter STANDARD_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Date-only formatter: yyyy-MM-dd
    private static final DateTimeFormatter DATE_ONLY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Time HH:mm
    private static final DateTimeFormatter TIME_HHMM_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    private DateUtil() {
        // Prevent instantiation.
    }

    /**
     * Get current system time as a string in the default standard format.
     *
     * @return formatted current time string, e.g., 2025-12-02 19:20:30
     */
    public static String now() {
        var now = LocalDateTime.now();
        return format(now);
    }

    /**
     * Format the given LocalDateTime to standard string: yyyy-MM-dd HH:mm:ss.
     *
     * @param time non-null LocalDateTime
     * @return formatted string
     * @throws IllegalArgumentException if time is null
     */
    public static String format(LocalDateTime time) {
        if (time == null) {
            throw new IllegalArgumentException("time must not be null");
        }
        return time.format(STANDARD_FORMATTER);
    }

    /**
     * Parse a standard string (yyyy-MM-dd HH:mm:ss) to LocalDateTime.
     *
     * @param timeStr non-null, non-blank string in standard format
     * @return parsed LocalDateTime
     * @throws IllegalArgumentException if input is null/blank
     * @throws java.time.format.DateTimeParseException if pattern does not match
     */
    public static LocalDateTime parse(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            throw new IllegalArgumentException("timeStr must not be null/blank");
        }
        return LocalDateTime.parse(timeStr, STANDARD_FORMATTER);
    }

    /**
     * Return a human-friendly relative time string for the given time relative to now.
     *
     * Logic:
     * - within 1 minute -> "刚刚"
     * - within 1 hour  -> "x 分钟前"
     * - within 24 hours -> "x 小时前"
     * - yesterday (calendar day) -> "昨天 HH:mm"
     * - earlier -> "yyyy-MM-dd"
     *
     * If the time is in the future, this method returns the standard formatted string.
     *
     * @param time non-null LocalDateTime
     * @return relative description string in Chinese
     */
    public static String getRelativeTime(LocalDateTime time) {
        Objects.requireNonNull(time, "time must not be null");

        var now = LocalDateTime.now();
        var duration = Duration.between(time, now);
        // If time is in the future, fall back to a stable representation.
        if (duration.isNegative()) {
            return format(time);
        }

        var seconds = duration.getSeconds();
        if (seconds < 60) {
            return "刚刚";
        }

        var minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + " 分钟前";
        }

        var hours = minutes / 60;
        if (hours < 24) {
            return hours + " 小时前";
        }

        // Beyond 24 hours: check if it's yesterday (calendar day comparison).
        var today = LocalDate.now();
        var date = time.toLocalDate();
        if (date.equals(today.minusDays(1))) {
            return "昨天 " + time.format(TIME_HHMM_FORMATTER);
        }

        // Older than yesterday: show date only.
        return time.format(DATE_ONLY_FORMATTER);
    }
}

