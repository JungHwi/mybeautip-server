package com.jocoos.mybeautip.client.flipfloplite.converter;

import com.jocoos.mybeautip.client.flipfloplite.dto.FflMemberInfo;
import com.jocoos.mybeautip.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface FlipFlopLiteConverter {

    @Mappings({
            @Mapping(target = "appUserId", source = "id"),
            @Mapping(target = "appUserName", source = "username"),
            @Mapping(target = "appUserProfileImgUrl", source = "avatarUrl"),
    })
    FflMemberInfo converts(Member entity);
}
