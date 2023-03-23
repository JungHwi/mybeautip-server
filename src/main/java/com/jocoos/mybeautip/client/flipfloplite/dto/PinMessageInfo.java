package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastPinMessage;
import com.jocoos.mybeautip.domain.member.dto.SimpleMemberInfo;
import lombok.Builder;

@Builder
public record PinMessageInfo(Long messageId,
                             String message,
                             SimpleMemberInfo createdBy) {

    public static PinMessageInfo pin(BroadcastPinMessage pinChat) {
        if (pinChat == null) {
            return null;
        }

        SimpleMemberInfo createdBy = new SimpleMemberInfo(pinChat.getMemberId(), pinChat.getUsername(), pinChat.getAvatarUrl());
        return PinMessageInfo.builder()
                .messageId(pinChat.getMessageId())
                .message(pinChat.getMessage())
                .createdBy(createdBy)
                .build();
    }
}
