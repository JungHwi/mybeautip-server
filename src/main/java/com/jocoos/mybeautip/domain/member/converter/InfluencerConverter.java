package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.domain.member.dto.InfluencerResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InfluencerConverter {

    List<InfluencerResponse> converts(List<Influencer> entity);
}
