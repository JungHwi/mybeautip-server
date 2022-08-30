package com.jocoos.mybeautip.domain.event.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipationResponse {

    private int result;

    public ParticipationResponse(int result) {
        this.result = result;
    }
}
