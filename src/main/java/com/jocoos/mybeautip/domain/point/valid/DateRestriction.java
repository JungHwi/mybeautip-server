package com.jocoos.mybeautip.domain.point.valid;

import com.jocoos.mybeautip.domain.point.code.DateLimit;
import lombok.Getter;

import static com.jocoos.mybeautip.domain.point.code.DateLimit.*;


@Getter
public class DateRestriction {
    private final DateLimit dateLimit;
    private final long limitNum;

    public static DateRestriction allTimeOnce() {
        return new DateRestriction(ALL_TIME_ONCE, 1);
    }

    public static DateRestriction day(long limitNum) {
        return new DateRestriction(DAY, limitNum);
    }

    private DateRestriction(DateLimit dateLimit, long limitNum) {
        this.dateLimit = dateLimit;
        this.limitNum = limitNum;
    }
}
