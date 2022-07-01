package com.jocoos.mybeautip.domain.point.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PointHistoryResponse {

    private int earnedPoint;

    private int expiryPoint;

    private List<PointHistoryListResponse> pointHistoryList;
}
