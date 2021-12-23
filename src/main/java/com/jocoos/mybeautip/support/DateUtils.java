package com.jocoos.mybeautip.support;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class DateUtils {
  public static ZoneId ZONE_SEOUL = ZoneId.of("Asia/Seoul");
  public static Date toDate(String longValue) {
    return toDate(longValue, ZoneId.of("GMT+9"));
  }

  public static Date toDate(String longValue, ZoneId zoneId) {
    LocalDateTime ldt =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(longValue)), ZoneId.systemDefault());
    ZoneId zone = zoneId == null ? ZoneId.systemDefault() : zoneId;
    return  Date.from(ldt.atZone(zone).toInstant());
  }

  public static LocalDate toLocalDate(Date date) {
    return date.toInstant().atZone(ZONE_SEOUL).toLocalDate();
  }

  private static LocalDate toLocalDate(Date date, ZoneId zoneId) {
    if (zoneId == null) {
      return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    return date.toInstant().atZone(zoneId).toLocalDate();
  }

  public static Date toDate(LocalDate dateToConvert) {
    return java.util.Date.from(dateToConvert.atStartOfDay()
        .atZone(ZoneId.systemDefault())
        .toInstant());
  }
}
