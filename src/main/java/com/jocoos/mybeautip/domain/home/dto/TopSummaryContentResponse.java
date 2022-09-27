package com.jocoos.mybeautip.domain.home.dto;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class TopSummaryContentResponse {
    private final Long categoryId;
    private final List<CommunityResponse> communities;
}
