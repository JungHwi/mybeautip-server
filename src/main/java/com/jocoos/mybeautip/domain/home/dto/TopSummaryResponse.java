package com.jocoos.mybeautip.domain.home.dto;

import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class TopSummaryResponse {
    private final List<CommunityCategoryResponse> categories;
    private final List<TopSummaryContentResponse> contents;
}
