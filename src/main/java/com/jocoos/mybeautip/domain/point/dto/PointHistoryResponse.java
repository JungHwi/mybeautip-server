package com.jocoos.mybeautip.domain.point.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.point.code.PointStatus;
import com.jocoos.mybeautip.member.order.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@Setter
public class PointHistoryResponse {

    private long id;

    private String title;

    private PointStatus status;

    private int point;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    @JsonIgnore
    private Order order;

    @JsonIgnore
    private Long eventId;

}
