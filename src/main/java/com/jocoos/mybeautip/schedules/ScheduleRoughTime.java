package com.jocoos.mybeautip.schedules;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

public class ScheduleRoughTime {
    private Date ahead;
    private Date behind;

    private ScheduleRoughTime(Instant instant, long amountTime, TemporalUnit unit) {
        ahead = Date.from(instant.minus(amountTime, unit));
        behind = Date.from(instant.plus(amountTime, unit));
    }

    public static ScheduleRoughTime now(long amountTime) {
        return new ScheduleRoughTime(Instant.now(), amountTime, ChronoUnit.MINUTES);
    }

    public Date getAheadTime() {
        return ahead;
    }

    public Date getBehindTime() {
        return behind;
    }

    public boolean checkVideo(Date scheduledTime) {
        return behind.after(scheduledTime);
    }
}
