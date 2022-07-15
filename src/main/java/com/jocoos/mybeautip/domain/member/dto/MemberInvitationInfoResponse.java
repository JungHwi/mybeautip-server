package com.jocoos.mybeautip.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInvitationInfoResponse {

    private final String title;

    private final String description;

    private final String shareSquareImageUrl;

    private final String shareRectangleImageUrl;

}
