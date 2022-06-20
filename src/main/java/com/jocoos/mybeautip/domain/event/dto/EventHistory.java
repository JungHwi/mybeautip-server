package com.jocoos.mybeautip.domain.event.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class EventHistory {

    private long id;
    private String title;
    private String description;
    private ZonedDateTime createdAt;
}
