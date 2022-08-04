package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommunityCategoryConverter {

    CommunityCategoryResponse convert(CommunityCategory entity);

    List<CommunityCategoryResponse> convert(List<CommunityCategory> entity);
}
