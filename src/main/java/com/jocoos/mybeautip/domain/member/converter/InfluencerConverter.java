package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.domain.member.dto.InfluencerResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InfluencerConverter {

    InfluencerResponse converts(Influencer entity);
}
