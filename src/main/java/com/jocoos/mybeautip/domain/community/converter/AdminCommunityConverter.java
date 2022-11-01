package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class AdminCommunityConverter {

    public abstract List<CommunityCategoryResponse> convert(List<CommunityCategory> adminCategories);

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "hint", ignore = true)
    protected abstract CommunityCategoryResponse convert(CommunityCategory adminCategory);
}
