package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.AssertTrue;

public record BroadcastPinMessageRequest(Long messageId,
                                         Long memberId,
                                         String message,
                                         String avatarUrl,
                                         String username,
                                         boolean isPin) {

    @JsonIgnore
    @AssertTrue(message = "if is pin true all fields must not null")
    public boolean isPinTrueFieldsMustNotNull() {
        if (isPin) {
            return messageId != null && memberId != null && message != null && avatarUrl != null && username != null;
        }
        return true;
    }
}
