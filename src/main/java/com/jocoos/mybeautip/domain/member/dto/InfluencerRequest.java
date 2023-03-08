package com.jocoos.mybeautip.domain.member.dto;

import com.jocoos.mybeautip.domain.member.code.InfluencerStatus;

import javax.validation.constraints.NotNull;
import java.util.Set;

public record InfluencerRequest(
        @NotNull Set<Long> ids,
        @NotNull InfluencerStatus status) {

}
