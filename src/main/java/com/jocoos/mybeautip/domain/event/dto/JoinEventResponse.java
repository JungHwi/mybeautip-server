package com.jocoos.mybeautip.domain.event.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JoinEventResponse {
    private String title;

    private String description;

    private LocalDateTime createdAt;
}
