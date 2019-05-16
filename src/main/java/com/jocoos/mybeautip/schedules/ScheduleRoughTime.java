package com.jocoos.mybeautip.schedules;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class ScheduleRoughTime {
    private Date ahead;
    private Date behind;

    public static ScheduleRoughTime now(long amountTime) {
        return new ScheduleRoughTime(Instant.now(), amountTime);
    }

    private ScheduleRoughTime(Instant instant, long amountTime) {
        ahead = Date.from(instant.minus(amountTime, ChronoUnit.MINUTES));
        behind = Date.from(instant.plus(amountTime, ChronoUnit.MINUTES));
    }

    public Date getAheadTime() {
        return ahead;
    }

    public Date getBehindTime() {
        return behind;
    }
}
