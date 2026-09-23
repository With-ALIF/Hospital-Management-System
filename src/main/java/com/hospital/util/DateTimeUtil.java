package com.hospital.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateTimeUtil {
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private DateTimeUtil() {
    }

    public static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim(), DATE);
        } catch (DateTimeParseException e) {
            throw new com.hospital.exception.InvalidDataException("Invalid date, expected yyyy-MM-dd: " + value);
        }
    }

    public static LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value.trim(), TIME);
        } catch (DateTimeParseException e) {
            throw new com.hospital.exception.InvalidDataException("Invalid time, expected HH:mm: " + value);
        }
    }

    public static String format(LocalDate date) {
        return date == null ? "—" : date.format(DATE);
    }

    public static String format(LocalTime time) {
        return time == null ? "—" : time.format(TIME);
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? "—" : dateTime.format(DATE_TIME);
    }

    public static boolean isPast(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }
}
