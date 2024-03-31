package ru.practicum.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtil {
  private static final String OVER_ISO_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

  public static LocalDateTime toLocalDateTime(String stringDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(OVER_ISO_DATE_TIME);
    return LocalDateTime.parse(stringDate, formatter);
  }

  public static LocalDateTime toLocalDateTime(String stringDate, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return LocalDateTime.parse(stringDate, formatter);
  }

  public static String toString(LocalDateTime localDateTime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(OVER_ISO_DATE_TIME);
    return localDateTime.format(formatter);
  }

  public static String toString(LocalDateTime localDateTime, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return localDateTime.format(formatter);
  }
}
