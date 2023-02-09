package com.jocoos.mybeautip.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.member.code.InfluencerStatus;
import lombok.Builder;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Builder
public record InfluencerResponse(InfluencerStatus influencerStatus,
                                 int broadcastCount,
                                 @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime earnedAt) {

}
