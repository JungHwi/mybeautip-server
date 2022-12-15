package com.jocoos.mybeautip.global.vo;

import lombok.Getter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.LocalTime.MAX;

@Getter
public class Day {
    private final ZonedDateTime startOfDay;
    private final ZonedDateTime endOfDay;

    public Day(LocalDate localDate, ZoneId zoneId) {
        this.startOfDay = localDate.atStartOfDay(zoneId);
        this.endOfDay = localDate.atTime(MAX).atZone(zoneId);
    }
}
