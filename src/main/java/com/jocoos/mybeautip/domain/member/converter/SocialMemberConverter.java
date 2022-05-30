package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import com.jocoos.mybeautip.security.SocialMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SocialMemberConverter {

    @Mappings({
            @Mapping(target = "id", source = "socialId"),
            @Mapping(target = "name", source = "username"),
            @Mapping(target = "picture", source = "avatarUrl"),
            @Mapping(target = "provider", source = "grantType"),
    })
    SocialMember convert(SignupRequest signupRequest);
}

