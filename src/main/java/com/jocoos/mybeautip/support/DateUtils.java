package com.jocoos.mybeautip.support;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class DateUtils {

  public static Date toDate(String longValue) {
    return toDate(longValue, ZoneId.of("GMT+9"));
  }

  public static Date toDate(String longValue, ZoneId zoneId) {
    LocalDateTime ldt =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(longValue)), ZoneId.systemDefault());
    ZoneId zone = zoneId == null ? ZoneId.systemDefault() : zoneId;
    return  Date.from(ldt.atZone(zone).toInstant());
  }
}
