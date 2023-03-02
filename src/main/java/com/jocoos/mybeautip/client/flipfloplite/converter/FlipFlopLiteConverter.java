package com.jocoos.mybeautip.client.flipfloplite.converter;

import com.jocoos.mybeautip.client.flipfloplite.dto.ExternalBroadcastInfo;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLMemberInfo;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLVideoRoomRequest;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLVideoRoomResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastCreateRequest;
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
    FFLMemberInfo converts(Member entity);

    @Mappings({
            @Mapping(target = "appUserId", source = "memberId"),
            @Mapping(target = "title", source = "request.title"),
            @Mapping(target = "scheduledAt", source = "request.startedAt"),
            @Mapping(target = "type", constant = "BROADCAST_RTMP"),
            @Mapping(target = "accessLevel", constant = "APP")
    })
    FFLVideoRoomRequest converts(BroadcastCreateRequest request, long memberId);

    ExternalBroadcastInfo converts(FFLVideoRoomResponse response);
}
