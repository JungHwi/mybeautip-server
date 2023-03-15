package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.global.util.MemberUtil;
import lombok.Builder;

@Builder
public record FFLGuestChatTokenRequest(String accessToken,
                                       String appUserId,
                                       String appUserName,
                                       String appUserProfileImageUrl) {

    public static FFLGuestChatTokenRequest from(String guestUsername) {
        return FFLGuestChatTokenRequest.builder()
                .appUserId(MemberUtil.getGuestUsernameWithoutPrefix(guestUsername))
                .appUserName(guestUsername)
                .build();
    }
}
