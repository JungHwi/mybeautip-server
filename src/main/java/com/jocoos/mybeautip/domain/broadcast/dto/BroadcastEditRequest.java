package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@RequiredArgsConstructor
public class BroadcastEditRequest {

    private final @NotNull String title;

    private final @NotBlank String thumbnailUrl;

    private final @NotNull Long categoryId;

    private final Boolean isStartNow;

    private final String notice;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime startedAt;

    @AssertTrue(message = "Broadcast request requires is_start_now is true or started_at is not null")
    public boolean validStartNowTrueOrStartedAtNotNull() {
        return isStartNow || startedAt != null;
    }
}
