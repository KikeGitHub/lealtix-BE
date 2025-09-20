package com.lealtixservice.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String formatDatefromLong(Long created) {
        LocalDateTime date = LocalDateTime.ofEpochSecond(created, 0, java.time.ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static String formatDatefromLongNext(Long created) {
        LocalDate date = LocalDate.ofEpochDay(created / 86400);
        date = date.plusMonths(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
}
