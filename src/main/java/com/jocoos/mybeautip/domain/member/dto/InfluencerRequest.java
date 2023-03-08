package com.jocoos.mybeautip.domain.member.dto;

import com.jocoos.mybeautip.domain.member.code.InfluencerStatus;

import java.util.List;

public record InfluencerRequest(List<Long> ids, InfluencerStatus status) {

}
