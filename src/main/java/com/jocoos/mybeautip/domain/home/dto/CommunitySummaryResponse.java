package com.jocoos.mybeautip.domain.home.dto;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class CommunitySummaryResponse {
        private final List<CommunityResponse> top;
        private final List<CommunityResponse> vote;
        private final List<CommunityResponse> blind;
}
