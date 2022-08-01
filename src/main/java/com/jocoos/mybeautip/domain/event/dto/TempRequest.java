package com.jocoos.mybeautip.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TempRequest {
    @NotNull
    private int id;

    private String name;

//    private MemberStatus status;

    private int cursor;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;
}
