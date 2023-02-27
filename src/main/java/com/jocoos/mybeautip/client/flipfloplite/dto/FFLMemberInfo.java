package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLEntityState;

public record FFLMemberInfo(long id,
                            FFLEntityState state,
                            String appUserId,
                            String appUserName,
                            String appUserProfileImgUrl) {
}
