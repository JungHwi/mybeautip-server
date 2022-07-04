package com.jocoos.mybeautip.domain.point.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointMonthlyHistoryResponse {

    private int earnedPoint;

    private int expiryPoint;
}
