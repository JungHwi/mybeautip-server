package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.domain.member.dto.MemberDetailRequest;
import com.jocoos.mybeautip.domain.member.dto.MemberDetailResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MemberDetailConverter {

    @Mappings({
            @Mapping(target = "ageGroup", ignore = true),
            @Mapping(target = "inviterTag", ignore = true),
            @Mapping(target = "changedTagInfo", ignore = true)
    })
    MemberDetailResponse convert(MemberDetail entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "inviterId", ignore = true)
    })
    MemberDetail convert(MemberDetailRequest request);
}
