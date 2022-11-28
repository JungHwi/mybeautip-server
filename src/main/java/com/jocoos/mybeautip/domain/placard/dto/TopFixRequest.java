package com.jocoos.mybeautip.domain.placard.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TopFixRequest {
    private final List<Long> ids;
    private final Boolean isTopFix;
}
