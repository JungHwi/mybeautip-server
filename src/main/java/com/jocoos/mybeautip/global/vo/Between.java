package com.jocoos.mybeautip.global.vo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.LocalTime.MAX;

public record Between(ZonedDateTime start, ZonedDateTime end) {
    public static Between day(LocalDate localDate, ZoneId zoneId) {
        return new Between(localDate.atStartOfDay(zoneId), localDate.atTime(MAX).atZone(zoneId));
    }
}
