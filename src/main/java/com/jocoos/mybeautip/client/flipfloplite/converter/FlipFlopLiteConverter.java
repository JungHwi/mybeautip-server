package com.jocoos.mybeautip.client.flipfloplite.converter;

import com.jocoos.mybeautip.client.flipfloplite.dto.*;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastViewerVo;
import com.jocoos.mybeautip.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.GUEST_TOKEN_PREFIX;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.GUEST_USERNAME_PREFIX;
import static com.jocoos.mybeautip.global.constant.SignConstant.EMPTY_STRING;

@Mapper(componentModel = "spring")
public abstract class FlipFlopLiteConverter {

    @Mappings({
            @Mapping(target = "appUserId", source = "id"),
            @Mapping(target = "appUserName", source = "username"),
            @Mapping(target = "appUserProfileImgUrl", source = "avatarUrl"),
    })
    public abstract FFLMemberInfo converts(Member entity);

    @Mappings({
            @Mapping(target = "appUserId", source = "memberId"),
            @Mapping(target = "scheduledAt", source = "startedAt"),
            @Mapping(target = "type", constant = "BROADCAST_RTMP"),
            @Mapping(target = "accessLevel", constant = "APP")
    })
    public abstract FFLVideoRoomRequest converts(Broadcast broadcast);

    @Mappings({
            @Mapping(target = "videoKey", source = "id"),
            @Mapping(target = "channelKey", source = "chat.channelKey")
    })
    public abstract ExternalBroadcastInfo converts(FFLVideoRoomResponse response);

    public abstract ChatTokenAndAppId converts(FFLChatTokenResponse response);

    public BroadcastViewerVo converts(FFLChatMemberInfo response) {
        BroadcastViewerType type = BroadcastViewerType.MEMBER;
        Long memberId = 0L;
        String username = "";
        if (response.appUserName().startsWith(GUEST_TOKEN_PREFIX)) {
            String guestId = response.appUserId().replace(GUEST_TOKEN_PREFIX, EMPTY_STRING);
            type = BroadcastViewerType.GUEST;
            memberId = Long.parseLong(guestId);
            username = String.format("%s%d", GUEST_USERNAME_PREFIX, memberId);
        } else {
            memberId = Long.parseLong(response.appUserId());
            username = response.appUserName();
        }

        return BroadcastViewerVo.builder()
                .type(type)
                .memberId(memberId)
                .username(username)
                .joinedAt(response.joinedAt())
                .build();
    }

    public abstract List<BroadcastViewerVo> convertToViewerVo(List<FFLChatMemberInfo> response);
}
