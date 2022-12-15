package com.jocoos.mybeautip.domain.member.vo;

import lombok.Getter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.LocalTime.MAX;

@Getter
public class ZonedDateTimeDay {
    private final ZonedDateTime startOfDay;
    private final ZonedDateTime endOfDay;

    public ZonedDateTimeDay(LocalDate localDate, ZoneId zoneId) {
        this.startOfDay = localDate.atStartOfDay(zoneId);
        this.endOfDay = localDate.atTime(MAX).atZone(zoneId);
    }
}
