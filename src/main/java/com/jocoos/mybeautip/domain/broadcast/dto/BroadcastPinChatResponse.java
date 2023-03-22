package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.jocoos.mybeautip.client.flipfloplite.dto.PinMessageInfo;

public record BroadcastPinChatResponse(@JsonUnwrapped PinMessageInfo messageInfo,
                                       boolean isPin) {

    public static BroadcastPinChatResponse pin(PinMessageInfo messageInfo) {
        return new BroadcastPinChatResponse(messageInfo, true);
    }

    public static BroadcastPinChatResponse noPin() {
        return new BroadcastPinChatResponse(null, false);
    }
}
