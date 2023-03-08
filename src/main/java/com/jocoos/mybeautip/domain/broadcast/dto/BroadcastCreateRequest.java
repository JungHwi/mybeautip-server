package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@AllArgsConstructor
public class BroadcastCreateRequest {

    @NotNull
    private final String title;

    @NotNull
    private final FileDto thumbnail;

    @NotNull
    private final Long categoryId;

    private final Boolean isStartNow;

    private final String notice;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime startedAt;

    @AssertTrue(message = "Broadcast request requires is_start_now is true or started_at is not null")
    public boolean validStartNowTrueOrStartedAtNotNull() {
        return isStartNow || startedAt != null;
    }
}
