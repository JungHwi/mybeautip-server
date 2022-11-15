package com.jocoos.mybeautip.domain.event.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventBatchResult {
    public int startCount;
    public int endCount;
}
