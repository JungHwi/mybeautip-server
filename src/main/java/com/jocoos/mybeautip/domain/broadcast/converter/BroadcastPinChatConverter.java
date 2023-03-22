package com.jocoos.mybeautip.domain.broadcast.converter;

import com.jocoos.mybeautip.client.flipfloplite.dto.PinMessageInfo;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPinMessageRequest;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastPinMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BroadcastPinChatConverter {

    @Mapping(target = "memberId", source = "request.memberId")
    @Mapping(target = "avatarUrl", source = "request.avatarUrl")
    BroadcastPinMessage toEntity(Broadcast broadcast, BroadcastPinMessageRequest request);

    PinMessageInfo toResponse(BroadcastPinMessage pinChat);

    BroadcastPinMessage merge(@MappingTarget BroadcastPinMessage message, BroadcastPinMessageRequest pinMessage);
}
