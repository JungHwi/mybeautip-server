package com.jocoos.mybeautip.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DormantNotificationResponse {

    private int beforeMonthCount;

    private int beforeWeekCount;

    private int beforeDayCount;

}
