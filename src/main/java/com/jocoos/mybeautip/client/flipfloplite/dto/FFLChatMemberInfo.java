package com.jocoos.mybeautip.client.flipfloplite.dto;

import java.time.ZonedDateTime;

public record FFLChatMemberInfo(String appUserId,
                                String appUserName,
                                String appUserProfileImgUrl,
                                ZonedDateTime joinedAt) {
}
